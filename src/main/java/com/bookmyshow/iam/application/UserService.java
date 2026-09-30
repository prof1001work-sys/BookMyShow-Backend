package com.bookmyshow.iam.application;

import com.bookmyshow.iam.api.dto.request.UserStatusUpdateRequest;
import com.bookmyshow.iam.api.dto.response.UserProfileResponse;
import com.bookmyshow.iam.api.publicapi.IamModuleApi;
import com.bookmyshow.iam.api.publicapi.UserPublicDto;
import com.bookmyshow.iam.domain.model.User;
import com.bookmyshow.iam.domain.model.UserStatus;
import com.bookmyshow.iam.domain.repository.UserRepository;
import com.bookmyshow.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service for user profile queries and admin account operations.
 * Implements IamModuleApi for safe inter-module communication.
 */
@Service
public class UserService implements IamModuleApi {

    private final UserRepository userRepository;
    private final AuthService authService;

    public UserService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return authService.mapToProfileResponse(user);
    }

    @Transactional
    public void updateUserStatus(UUID userId, UserStatusUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setStatus(request.status());
        userRepository.save(user);
    }

    // =========================================================================
    // IamModuleApi Implementation (Cross-module boundary)
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public Optional<UserPublicDto> findUserById(UUID userId) {
        return userRepository.findById(userId).map(this::mapToPublicDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserActive(UUID userId) {
        return userRepository.findById(userId)
                .map(u -> u.getStatus() == UserStatus.ACTIVE && u.getDeletedAt() == null)
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID userId) {
        return userRepository.findById(userId).isPresent();
    }

    private UserPublicDto mapToPublicDto(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        return new UserPublicDto(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                roles,
                user.getStatus().name()
        );
    }
}
