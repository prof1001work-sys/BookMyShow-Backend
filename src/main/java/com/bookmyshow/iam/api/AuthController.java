package com.bookmyshow.iam.api;

import com.bookmyshow.iam.api.dto.request.LoginRequest;
import com.bookmyshow.iam.api.dto.request.LogoutRequest;
import com.bookmyshow.iam.api.dto.request.PasswordResetConfirmRequest;
import com.bookmyshow.iam.api.dto.request.PasswordResetOtpRequest;
import com.bookmyshow.iam.api.dto.request.RefreshTokenRequest;
import com.bookmyshow.iam.api.dto.request.RegisterRequest;
import com.bookmyshow.iam.api.dto.response.AuthResponse;
import com.bookmyshow.iam.api.dto.response.TokenRefreshResponse;
import com.bookmyshow.iam.api.dto.response.UserProfileResponse;
import com.bookmyshow.iam.application.AuthService;
import com.bookmyshow.iam.application.UserService;
import com.bookmyshow.iam.infrastructure.security.SecurityUtils;
import com.bookmyshow.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication & IAM", description = "Endpoints for user registration, authentication, token rotation, and password reset")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new customer or theatre owner account and returns initial JWT access and refresh tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "User registered successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Validates credentials and returns JWT access token and refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Authentication successful"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rotate refresh token", description = "Exchanges a valid refresh token for a newly rotated refresh token and fresh access token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenRefreshResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Blacklists the current access token in Redis and revokes the refresh token")
    @SecurityRequirement(name = "BearerAuthentication")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestBody(required = false) LogoutRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        authService.logout(request, authHeader);
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }

    @PostMapping("/password-reset/request")
    @Operation(summary = "Request password reset OTP", description = "Generates a 6-digit OTP code for password reset")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(@Valid @RequestBody PasswordResetOtpRequest request) {
        authService.requestPasswordReset(request);
        return ResponseEntity.ok(ApiResponse.success(null, "If an account exists with this email, a reset code has been sent"));
    }

    @PostMapping("/password-reset/confirm")
    @Operation(summary = "Confirm password reset", description = "Validates OTP code and updates user password")
    public ResponseEntity<ApiResponse<Void>> confirmPasswordReset(@Valid @RequestBody PasswordResetConfirmRequest request) {
        authService.confirmPasswordReset(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successfully. Please login with your new password."));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Retrieves profile information for the authenticated user")
    @SecurityRequirement(name = "BearerAuthentication")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser() {
        UUID userId = SecurityUtils.getCurrentUserId();
        UserProfileResponse response = userService.getCurrentUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "User profile retrieved successfully"));
    }
}
