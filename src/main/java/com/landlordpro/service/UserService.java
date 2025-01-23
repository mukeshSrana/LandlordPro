package com.landlordpro.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);
        user.addRole(UserRole.ROLE_USER);

        userRepository.save(user);
    }
}


