package com.landlordpro.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableMethodSecurity // Enables @PreAuthorize, @Secured, etc.
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) { this.customUserDetailsService = customUserDetailsService; }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SessionRegistry sessionRegistry) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/users/register", "/*.css", "/about", "/contact","/privacyPolicy", "auth-check.js").permitAll()
                //.requestMatchers("/register", "/*.css", "/about", "/*.js").permitAll()
                //.requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/*.css", "/about", "/*.js").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")  // Specify the custom login page
                .loginProcessingUrl("/login")  // Where the form is submitted
                .defaultSuccessUrl("/", true)  // Redirect after successful login
                //.failureUrl("/login?error")
                .failureHandler(customAuthenticationFailureHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")  // Set logout URL
                .addLogoutHandler((request, response, authentication) -> {
                    if (authentication != null) {
                        sessionRegistry.removeSessionInformation(request.getSession().getId());  // Remove session from registry
                    }
                })
                .logoutSuccessHandler((request, response, authentication) -> {
                    if (authentication != null) {
                        sessionRegistry.removeSessionInformation(request.getSession().getId());
                    }
                    response.sendRedirect("/login?logout");
                })
                .invalidateHttpSession(true)  // Invalidate the session on logout
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")  // Optional: Delete the session cookie
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)  // Session is created only if needed
                .sessionFixation().newSession()  // Prevent session fixation by creating a new session
                .invalidSessionUrl("/login?sessionExpired")  // Redirect to login if session is invalid
                .maximumSessions(1)  // Prevent multiple sessions for a user
                .maxSessionsPreventsLogin(true)  // Prevent login if maximum sessions reached
                .sessionRegistry(sessionRegistry)  // Attach session registry for tracking sessions
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/logout") // Disable CSRF for logout only
            );

        return http.build();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
            .userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}

