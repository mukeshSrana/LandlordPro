package com.landlordpro.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String errorMessage = "Invalid username or password.";

        if (exception instanceof UsernameNotFoundException) {
            errorMessage = exception.getMessage();
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "Incorrect username or password.";
        } else if (exception instanceof DisabledException) {
            errorMessage = "Your account has been disabled. Contact support.";
        } else if (exception instanceof LockedException) {
            errorMessage = "Your account has been locked due to multiple failed login attempts.";
        } else if (exception instanceof CredentialsExpiredException) {
            errorMessage = "Your password has expired. Please reset your password.";
        } else if (exception instanceof AccountExpiredException) {
            errorMessage = exception.getMessage();
        }

        // Store error in session (Flash Attribute)
        request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", errorMessage);

        // Redirect to login page
        response.sendRedirect("/login");
    }
}

