package com.landlordpro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.landlordpro.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}

