package com.bookmyshow.iam.api;

import com.bookmyshow.iam.api.dto.request.UserStatusUpdateRequest;
import com.bookmyshow.iam.api.dto.response.UserProfileResponse;
import com.bookmyshow.iam.application.UserService;
import com.bookmyshow.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "Endpoints for user profile retrieval and administrative user operations")
@SecurityRequirement(name = "BearerAuthentication")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPORT')")
    @Operation(summary = "Get user by ID", description = "Administrative query to fetch user details by UUID")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(@PathVariable UUID userId) {
        UserProfileResponse response = userService.getCurrentUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user status", description = "Admin-only operation to activate, suspend, or deactivate user account")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UserStatusUpdateRequest request
    ) {
        userService.updateUserStatus(userId, request);
        return ResponseEntity.ok(ApiResponse.success(null, "User status updated to " + request.status()));
    }
}
