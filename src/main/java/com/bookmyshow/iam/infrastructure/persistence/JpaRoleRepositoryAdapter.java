package com.bookmyshow.iam.infrastructure.persistence;

import com.bookmyshow.iam.domain.model.Role;
import com.bookmyshow.iam.domain.model.RoleName;
import com.bookmyshow.iam.domain.repository.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class JpaRoleRepositoryAdapter implements RoleRepository {

    private final SpringDataRoleRepository springDataRoleRepository;

    public JpaRoleRepositoryAdapter(SpringDataRoleRepository springDataRoleRepository) {
        this.springDataRoleRepository = springDataRoleRepository;
    }

    @Override
    public Role save(Role role) {
        return springDataRoleRepository.save(role);
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return springDataRoleRepository.findById(id);
    }

    @Override
    public Optional<Role> findByName(RoleName name) {
        return springDataRoleRepository.findByName(name);
    }
}
