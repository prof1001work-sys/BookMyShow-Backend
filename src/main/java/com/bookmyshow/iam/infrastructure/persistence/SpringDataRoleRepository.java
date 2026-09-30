package com.bookmyshow.iam.infrastructure.persistence;

import com.bookmyshow.iam.domain.model.Role;
import com.bookmyshow.iam.domain.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataRoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(RoleName name);
}
