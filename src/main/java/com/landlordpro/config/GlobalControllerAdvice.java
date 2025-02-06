package com.landlordpro.config;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.landlordpro.dto.constants.UserRole;
import com.landlordpro.security.CustomUserDetails;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addAuthStatus(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null
            && authentication.isAuthenticated()
            && !"anonymousUser".equals(authentication.getPrincipal());
        if (isAuthenticated) {
            CustomUserDetails userDetails = currentUser(authentication);
            if (!userDetails.isDeleted()) {
            model.addAttribute("name", userDetails.getName());
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("userRole",  getUserRole(userDetails).getDescription());
            } else {
                model.addAttribute("error", "Your account has been deleted.");
                SecurityContextHolder.clearContext();
                isAuthenticated = false;
            }
        }
        model.addAttribute("isAuthenticated", isAuthenticated);
    }

    private static UserRole getUserRole(CustomUserDetails userDetails) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        UserRole userRole = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .map(s -> UserRole.valueOf(s))
            .findFirst().orElseThrow();
        return userRole;
    }

    private CustomUserDetails currentUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }
        throw new IllegalStateException("Unexpected principal type");
    }
}

