package com.bookmyshow.iam.domain.repository;

import com.bookmyshow.iam.domain.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository contract for User aggregate.
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
