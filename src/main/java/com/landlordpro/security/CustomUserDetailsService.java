package com.landlordpro.security;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.landlordpro.domain.User;
import com.landlordpro.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch the user from the database
        User user = userRepository.findByUsername(username)
            .filter(u -> !u.isDeleted())
            .orElseThrow(() -> new UsernameNotFoundException("User not found or has been deleted."));

        // Return a new UserDetails object with the necessary details
        return new CustomUserDetails(
            user.getId(),
            user.getName(),
            user.getUsername(),
            user.getPassword(),
            user.isDeleted(),
            user.isEnabled(),
            user.isAccountNonExpired(),
            user.isCredentialsNonExpired(),
            user.isAccountNonLocked(),
            Collections.singletonList(new SimpleGrantedAuthority(user.getUserRole()))
        );
    }
}
