//package com.example.taskapp.security_disabled;
//
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.core.userdetails.*;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//
///**
// * Minimal, safe UserDetailsService used only for local smoke tests (no DB access).
// * Replace with your real implementation after testing.
// */
//@Service
//@Profile("!prod") // optional: avoid running in production if you use profiles
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    public UserDetailsServiceImpl() {
//        // no DB, no repository injection — safe for smoke tests
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        // Return a dummy user with no authorities. This is only for local smoke-testing.
//        // Password is empty because we won't authenticate in this smoke test.
//        return new User(username == null ? "user" : username, "", new ArrayList<>());
//    }
//}
