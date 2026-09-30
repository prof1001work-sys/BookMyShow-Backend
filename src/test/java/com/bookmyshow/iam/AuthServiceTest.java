package com.bookmyshow.iam;

import com.bookmyshow.iam.api.dto.request.LoginRequest;
import com.bookmyshow.iam.api.dto.request.RegisterRequest;
import com.bookmyshow.iam.api.dto.response.AuthResponse;
import com.bookmyshow.iam.application.AuthService;
import com.bookmyshow.iam.domain.model.Role;
import com.bookmyshow.iam.domain.model.RoleName;
import com.bookmyshow.iam.domain.model.User;
import com.bookmyshow.iam.domain.repository.OtpVerificationRepository;
import com.bookmyshow.iam.domain.repository.RefreshTokenRepository;
import com.bookmyshow.iam.domain.repository.RoleRepository;
import com.bookmyshow.iam.domain.repository.UserRepository;
import com.bookmyshow.iam.infrastructure.cache.RedisTokenBlacklistService;
import com.bookmyshow.iam.infrastructure.security.JwtTokenProvider;
import com.bookmyshow.shared.exception.ConflictException;
import com.bookmyshow.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private OtpVerificationRepository otpVerificationRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RedisTokenBlacklistService redisTokenBlacklistService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                roleRepository,
                refreshTokenRepository,
                otpVerificationRepository,
                passwordEncoder,
                jwtTokenProvider,
                redisTokenBlacklistService,
                eventPublisher
        );
    }

    @Test
    @DisplayName("Successfully registers a new customer")
    void shouldRegisterNewUserSuccessfully() {
        RegisterRequest request = new RegisterRequest(
                "john.doe@example.com",
                "Password@123",
                "+919876543210",
                "John",
                "Doe",
                RoleName.ROLE_CUSTOMER
        );

        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(userRepository.existsByPhoneNumber("+919876543210")).thenReturn(false);
        when(passwordEncoder.encode("Password@123")).thenReturn("encodedPasswordHash");

        Role customerRole = new Role(RoleName.ROLE_CUSTOMER, "Customer role");
        when(roleRepository.findByName(RoleName.ROLE_CUSTOMER)).thenReturn(Optional.of(customerRole));

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });

        when(jwtTokenProvider.generateAccessToken(any(), anyString(), any())).thenReturn("mock-access-token");
        when(jwtTokenProvider.getAccessTokenExpirationMinutes()).thenReturn(15L);
        when(jwtTokenProvider.getRefreshTokenExpirationDays()).thenReturn(7L);

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("mock-access-token");
        assertThat(response.user().email()).isEqualTo("john.doe@example.com");
        assertThat(response.user().firstName()).isEqualTo("John");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPasswordHash()).isEqualTo("encodedPasswordHash");
    }

    @Test
    @DisplayName("Throws ConflictException when email is already registered")
    void shouldThrowConflictWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "existing@example.com",
                "Password@123",
                null,
                "Jane",
                "Doe",
                null
        );

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Email is already registered");
    }

    @Test
    @DisplayName("Throws UnauthorizedException on invalid login password")
    void shouldThrowUnauthorizedOnBadPassword() {
        LoginRequest request = new LoginRequest("user@example.com", "wrongPassword");

        User user = new User("user@example.com", null, "correctHash", "User", "One");
        user.setId(UUID.randomUUID());

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "correctHash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid email or password");
    }
}
