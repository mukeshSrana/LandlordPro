package com.landlordpro.service;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.landlordpro.domain.User;
import com.landlordpro.dto.UserDto;
import com.landlordpro.dto.UserRegistrationDTO;
import com.landlordpro.dto.enums.UserRole;
import com.landlordpro.mapper.UserMapper;
import com.landlordpro.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Transactional
    public void changePassword(String userName, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(userName)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // Hash and update new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Restricting access to only users with ROLE_ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDto> getAllUsers() {
        return userMapper.toDTOList(userRepository.findAll());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean updateUser(UserDto userDto) {
        // Retrieve the existing user entity by ID
        User existingUser = userRepository.findById(userDto.getId())
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userDto.getId()));

        // Map the fields from the DTO to the existing entity
        userMapper.updateEntityFromDto(userDto, existingUser);

        // Save the updated entity
        try {
            userRepository.save(existingUser);
        } catch (DataIntegrityViolationException ex) {
            // Handle constraint violation, e.g., duplicate username
            throw new RuntimeException("Constraint violation while updating user", ex);
        } catch (Exception ex) {
            // General exception handling
            throw new RuntimeException("Unexpected error while updating user", ex);
        }
        return true;
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
        user.setAcceptConsent(registrationDTO.isAcceptConsent());
        user.setAcceptTenantDataResponsibility(registrationDTO.isAcceptTenantDataResponsibility());

        user.setEnabled(false);
        user.setDeleted(false);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);
        user.setUserRole(UserRole.ROLE_LANDLORD.name());

        userRepository.save(user);
    }
}


