package com.bookmyshow.iam.infrastructure.persistence;

import com.bookmyshow.iam.domain.model.User;
import com.bookmyshow.iam.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository springDataUserRepository;

    public JpaUserRepositoryAdapter(SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
    }

    @Override
    public User save(User user) {
        return springDataUserRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springDataUserRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataUserRepository.findByEmailWithRoles(email);
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return springDataUserRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataUserRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return springDataUserRepository.existsByPhoneNumber(phoneNumber);
    }
}
