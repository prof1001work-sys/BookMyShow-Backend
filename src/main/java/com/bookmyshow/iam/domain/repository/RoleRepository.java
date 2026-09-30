package com.bookmyshow.iam.domain.repository;

import com.bookmyshow.iam.domain.model.Role;
import com.bookmyshow.iam.domain.model.RoleName;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository contract for Role entity.
 */
public interface RoleRepository {

    Role save(Role role);

    Optional<Role> findById(UUID id);

    Optional<Role> findByName(RoleName name);
}
