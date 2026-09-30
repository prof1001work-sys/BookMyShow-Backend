package com.bookmyshow.iam.application;

import com.bookmyshow.iam.api.dto.request.LoginRequest;
import com.bookmyshow.iam.api.dto.request.LogoutRequest;
import com.bookmyshow.iam.api.dto.request.PasswordResetConfirmRequest;
import com.bookmyshow.iam.api.dto.request.PasswordResetOtpRequest;
import com.bookmyshow.iam.api.dto.request.RefreshTokenRequest;
import com.bookmyshow.iam.api.dto.request.RegisterRequest;
import com.bookmyshow.iam.api.dto.response.AuthResponse;
import com.bookmyshow.iam.api.dto.response.TokenRefreshResponse;
import com.bookmyshow.iam.api.dto.response.UserProfileResponse;
import com.bookmyshow.iam.domain.event.UserPasswordChangedEvent;
import com.bookmyshow.iam.domain.event.UserRegisteredEvent;
import com.bookmyshow.iam.domain.model.OtpPurpose;
import com.bookmyshow.iam.domain.model.OtpVerification;
import com.bookmyshow.iam.domain.model.RefreshToken;
import com.bookmyshow.iam.domain.model.Role;
import com.bookmyshow.iam.domain.model.RoleName;
import com.bookmyshow.iam.domain.model.User;
import com.bookmyshow.iam.domain.model.UserStatus;
import com.bookmyshow.iam.domain.repository.OtpVerificationRepository;
import com.bookmyshow.iam.domain.repository.RefreshTokenRepository;
import com.bookmyshow.iam.domain.repository.RoleRepository;
import com.bookmyshow.iam.domain.repository.UserRepository;
import com.bookmyshow.iam.infrastructure.cache.RedisTokenBlacklistService;
import com.bookmyshow.iam.infrastructure.security.JwtTokenProvider;
import com.bookmyshow.shared.exception.BusinessRuleException;
import com.bookmyshow.shared.exception.ConflictException;
import com.bookmyshow.shared.exception.ResourceNotFoundException;
import com.bookmyshow.shared.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * IAM Application Service orchestrating registration, login, token rotation,
 * logout, and password recovery workflows.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String DUMMY_HASH = "$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenBlacklistService redisTokenBlacklistService;
    private final ApplicationEventPublisher eventPublisher;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            RefreshTokenRepository refreshTokenRepository,
            OtpVerificationRepository otpVerificationRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            RedisTokenBlacklistService redisTokenBlacklistService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.otpVerificationRepository = otpVerificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTokenBlacklistService = redisTokenBlacklistService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email().toLowerCase().trim())) {
            throw new ConflictException("Email is already registered: " + request.email());
        }
        if (StringUtils.hasText(request.phoneNumber()) && userRepository.existsByPhoneNumber(request.phoneNumber().trim())) {
            throw new ConflictException("Phone number is already registered: " + request.phoneNumber());
        }

        User user = new User(
                request.email().toLowerCase().trim(),
                request.phoneNumber() != null ? request.phoneNumber().trim() : null,
                passwordEncoder.encode(request.password()),
                request.firstName().trim(),
                request.lastName().trim()
        );

        RoleName roleName = request.role() != null ? request.role() : RoleName.ROLE_CUSTOMER;
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName, "Role for " + roleName.name())));
        user.addRole(role);

        User savedUser = userRepository.save(user);

        // Issue tokens
        List<String> roleStrings = List.of(role.getName().name());
        String accessToken = jwtTokenProvider.generateAccessToken(savedUser.getId(), savedUser.getEmail(), roleStrings);
        String rawRefreshToken = generateSecureRandomToken();
        saveRefreshToken(savedUser, rawRefreshToken, null, null);

        eventPublisher.publishEvent(new UserRegisteredEvent(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                Set.of(role.getName().name())
        ));

        UserProfileResponse profileResponse = mapToProfileResponse(savedUser);
        long expiresInSeconds = jwtTokenProvider.getAccessTokenExpirationMinutes() * 60;
        return new AuthResponse(accessToken, rawRefreshToken, expiresInSeconds, profileResponse);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.email().toLowerCase().trim();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            // Constant-time execution to protect against timing attacks / user enumeration
            passwordEncoder.matches("dummy", DUMMY_HASH);
            throw new UnauthorizedException("Invalid email or password");
        }

        User user = userOptional.get();

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new BusinessRuleException("Account is suspended. Please contact support.");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), roles);
        String rawRefreshToken = generateSecureRandomToken();
        saveRefreshToken(user, rawRefreshToken, null, null);

        long expiresInSeconds = jwtTokenProvider.getAccessTokenExpirationMinutes() * 60;
        return new AuthResponse(accessToken, rawRefreshToken, expiresInSeconds, mapToProfileResponse(user));
    }

    @Transactional
    public TokenRefreshResponse refreshToken(RefreshTokenRequest request) {
        String tokenHash = hashToken(request.refreshToken());
        RefreshToken token = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        User user = token.getUser();

        // Compromise detection: if a revoked/replaced token is presented again, invalidate all sessions
        if (token.isRevoked() || token.getReplacedByTokenHash() != null) {
            log.warn("SECURITY ALERT: Attempted reuse of revoked refresh token for user: {}. Revoking all sessions.", user.getId());
            refreshTokenRepository.revokeAllByUserId(user.getId());
            throw new UnauthorizedException("Compromised refresh token detected. All active sessions have been terminated.");
        }

        if (token.isExpired()) {
            throw new UnauthorizedException("Refresh token has expired. Please log in again.");
        }

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new BusinessRuleException("Account is suspended.");
        }

        // Rotate token
        String newRawRefreshToken = generateSecureRandomToken();
        String newHash = hashToken(newRawRefreshToken);
        token.revoke(newHash);
        refreshTokenRepository.save(token);

        saveRefreshToken(user, newRawRefreshToken, token.getDeviceInfo(), token.getIpAddress());

        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), roles);

        long expiresInSeconds = jwtTokenProvider.getAccessTokenExpirationMinutes() * 60;
        return new TokenRefreshResponse(newAccessToken, newRawRefreshToken, expiresInSeconds);
    }

    @Transactional
    public void logout(LogoutRequest request, String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                String jti = jwtTokenProvider.extractJti(token);
                Instant expiry = jwtTokenProvider.extractExpiration(token);
                Duration remaining = Duration.between(Instant.now(), expiry);
                redisTokenBlacklistService.blacklistToken(jti, remaining);
            }
        }

        if (request != null && StringUtils.hasText(request.refreshToken())) {
            String tokenHash = hashToken(request.refreshToken());
            refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(rt -> {
                rt.revoke(null);
                refreshTokenRepository.save(rt);
            });
        }
    }

    @Transactional
    public void requestPasswordReset(PasswordResetOtpRequest request) {
        String email = request.email().toLowerCase().trim();
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            // Return quietly to avoid email enumeration
            return;
        }

        User user = userOptional.get();

        // Generate 6-digit OTP
        int otp = 100000 + SECURE_RANDOM.nextInt(900000);
        String otpHash = hashToken(String.valueOf(otp));
        Instant expiresAt = Instant.now().plus(10, ChronoUnit.MINUTES);

        otpVerificationRepository.deleteByUserIdAndPurpose(user.getId(), OtpPurpose.PASSWORD_RESET);
        otpVerificationRepository.save(new OtpVerification(user, otpHash, OtpPurpose.PASSWORD_RESET, expiresAt));

        log.info("Generated Password Reset OTP for user: {} [DEV CODE: {}]", user.getId(), otp);
    }

    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmRequest request) {
        String email = request.email().toLowerCase().trim();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        OtpVerification verification = otpVerificationRepository
                .findLatestByUserIdAndPurpose(user.getId(), OtpPurpose.PASSWORD_RESET)
                .orElseThrow(() -> new BusinessRuleException("No pending password reset request found."));

        if (!verification.isValid()) {
            throw new BusinessRuleException("Password reset OTP is expired or has exceeded the maximum attempts.");
        }

        String inputHash = hashToken(request.otp().trim());
        if (!verification.getOtpHash().equals(inputHash)) {
            verification.recordAttempt();
            otpVerificationRepository.save(verification);
            throw new BusinessRuleException("Invalid OTP code. Attempts remaining: " + (OtpVerification.MAX_ATTEMPTS - verification.getAttempts()));
        }

        verification.markVerified();
        otpVerificationRepository.save(verification);

        // Update password and revoke all sessions
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        refreshTokenRepository.revokeAllByUserId(user.getId());

        eventPublisher.publishEvent(new UserPasswordChangedEvent(user.getId(), user.getEmail()));
    }

    private void saveRefreshToken(User user, String rawToken, String deviceInfo, String ipAddress) {
        String hash = hashToken(rawToken);
        Instant expiry = Instant.now().plus(jwtTokenProvider.getRefreshTokenExpirationDays(), ChronoUnit.DAYS);
        RefreshToken refreshToken = new RefreshToken(user, hash, expiry, deviceInfo, ipAddress);
        refreshTokenRepository.save(refreshToken);
    }

    private String generateSecureRandomToken() {
        byte[] randomBytes = new byte[48];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", e);
        }
    }

    public UserProfileResponse mapToProfileResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getPhoneNumber(),
                roleNames,
                user.getStatus().name(),
                user.isEmailVerified(),
                user.isPhoneVerified()
        );
    }
}
