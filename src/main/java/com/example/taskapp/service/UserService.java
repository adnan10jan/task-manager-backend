package com.example.taskapp.service;

import com.example.taskapp.model.User;
import com.example.taskapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User register(String username, String email, String password) {
        if (repo.existsByUsername(username)) {
            throw new IllegalArgumentException("Username taken");
        }
        if (repo.existsByEmail(email)) {
            throw new IllegalArgumentException("Email taken");
        }

        User u = new User();
        u.setUsername(username);
        u.setEmail(email);

        // ⚠️ TEMP: store password as plain text (ONLY FOR DEV)
        u.setPassword(password);

        u.setRoles(Set.of("ROLE_USER"));
        return repo.save(u);
    }

    public User login(String username, String password) {
        User user = repo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password");
        }

        return user;
    }
}
