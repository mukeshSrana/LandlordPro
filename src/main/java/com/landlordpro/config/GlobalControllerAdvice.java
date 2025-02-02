package com.landlordpro.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.landlordpro.security.CustomUserDetails;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addAuthStatus(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal());
        if (isAuthenticated) {
            CustomUserDetails userDetails = currentUser(authentication);
            model.addAttribute("username", userDetails.getName());
        }
        model.addAttribute("isAuthenticated", isAuthenticated);
    }

    private CustomUserDetails currentUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return customUserDetails;
        }
        throw new IllegalStateException("Unexpected principal type");
    }
}

