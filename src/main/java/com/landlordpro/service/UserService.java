package com.landlordpro.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.landlordpro.domain.User;
import com.landlordpro.dto.UserRegistrationDTO;
import com.landlordpro.dto.UserRole;
import com.landlordpro.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Restricting access to only users with ROLE_ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean isUserExists(String email) {
        return userRepository.findByUsername(email).isPresent();
    }

    public void registerUser(UserRegistrationDTO registrationDTO) {
        if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setName(registrationDTO.getName());
        user.setMobileNumber(registrationDTO.getMobileNumber());
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);
        user.addRole(UserRole.ROLE_USER);

        userRepository.save(user);
    }
}


