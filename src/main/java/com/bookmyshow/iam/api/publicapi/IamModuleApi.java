package com.bookmyshow.iam.api.publicapi;

import java.util.Optional;
import java.util.UUID;

/**
 * Public Java interface exposed by IAM module to other modules in the Modular Monolith.
 */
public interface IamModuleApi {

    Optional<UserPublicDto> findUserById(UUID userId);

    boolean isUserActive(UUID userId);

    boolean existsById(UUID userId);
}
