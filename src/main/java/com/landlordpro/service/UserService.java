package com.landlordpro.service;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.landlordpro.domain.User;
import com.landlordpro.dto.PasswordChangeDto;
import com.landlordpro.dto.UserDto;
import com.landlordpro.dto.UserRegistrationDTO;
import com.landlordpro.dto.enums.UserRole;
import com.landlordpro.mapper.UserMapper;
import com.landlordpro.repository.UserRepository;

import jakarta.validation.ConstraintViolationException;

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
    public void changePassword(PasswordChangeDto passwordChangeDto) {
        User user = userRepository.findByUsername(passwordChangeDto.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("Incorrect username or password"));

        // Validate old password
        if (!passwordEncoder.matches(passwordChangeDto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect username or password");
        }

        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        // Hash and update new password
        user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
        userRepository.save(user);
    }

    // Restricting access to only users with ROLE_ADMIN
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDto> getAllUsers() {
        return userMapper.toDTOList(userRepository.findAll());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateUser(UserDto userDto) {
        User existingUser = userRepository.findById(userDto.getId())
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userDto.getId()));

        userMapper.updateEntityFromDto(userDto, existingUser);

        try {
            userRepository.save(existingUser);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Constraint violation while updating user", ex);
        } catch (Exception ex) {
            throw new RuntimeException("Unexpected error while updating user", ex);
        }
    }

    public boolean isUserExists(String email) {
        return userRepository.findByUsername(email).isPresent();
    }

    public void registerUser(UserRegistrationDTO registrationDTO) {
        if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        try {
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
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Username already exists.");
        } catch (ConstraintViolationException e) {
            throw new IllegalArgumentException("Invalid user data: " + e.getMessage());
        } catch (JpaSystemException e) {
            throw new RuntimeException("Database error occurred while registering user.");
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred. Please try again later.");
        }
    }
}


