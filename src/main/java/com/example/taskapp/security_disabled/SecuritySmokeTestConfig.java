//package com.example.taskapp.security_disabled;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
///**
// * TEMPORARY test-only security beans so the app can start without full security/JPA setup.
// * - AuthenticationManager here simply marks the incoming Authentication as authenticated (no checks).
// * - PasswordEncoder provided so any code requiring it will be satisfied.
// *
// * IMPORTANT: remove or replace this with proper security config (JWT + real AuthenticationManager)
// * when you enable database + real security.
// */
//@Configuration
//public class SecuritySmokeTestConfig {
//
//    @Bean
//    public AuthenticationManager authenticationManager() {
//        return new AuthenticationManager() {
//            @Override
//            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//                authentication.setAuthenticated(true);
//                return authentication;
//            }
//        };
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
