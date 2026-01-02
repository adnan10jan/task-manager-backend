package com.example.taskapp.service;

import com.example.taskapp.model.User;
import com.example.taskapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.taskapp.exception.ApiException;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public User register(String username, String email, String password) {

        if (repo.existsByUsername(username))
            throw new ApiException("Username already taken");

        if (repo.existsByEmail(email))
            throw new ApiException("Email already registered");


        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(encoder.encode(password));
        u.setRoles(Set.of("ROLE_USER"));
//        u.setRoles(Set.of(Role.ROLE_USER));
        return repo.save(u);
    }

    public User login(String username, String rawPassword) {
        User user = repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        return user;
    }
}
