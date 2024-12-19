package com.landlordpro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.landlordpro.domain.User;
import com.landlordpro.dto.UserDto;
import com.landlordpro.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean isUserExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setName(userDto.getName());
        user.setMobileNr(userDto.getMobileNr());
        userRepository.save(user);
    }
}

