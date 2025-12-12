package com.example.taskapp.service;

import com.example.taskapp.model.User;
import com.example.taskapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        if (repo.existsByUsername(username)) throw new IllegalArgumentException("Username taken");
        if (repo.existsByEmail(email)) throw new IllegalArgumentException("Email taken");

        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(encoder.encode(password)); // bcrypt hashing
        u.setRoles(Set.of("USER"));

        return repo.save(u);
    }

    // TEMPORARY LOGIN (no JWT)
    public User login(String username, String password) {
        User u = repo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!encoder.matches(password, u.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return u;  // return user object for temporary login (we will replace with token later)
    }
}
