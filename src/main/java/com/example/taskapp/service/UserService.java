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

    public UserService(UserRepository repo ) {
        this.repo = repo;
        this.encoder = null;
    }

    public User register(String username, String email, String password) {
        if (repo.existsByUsername(username)) throw new IllegalArgumentException("Username taken");
        if (repo.existsByEmail(email)) throw new IllegalArgumentException("Email taken");
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(encoder.encode(password));
        u.setRoles(Set.of("ROLE_USER"));
        return repo.save(u);
    }
}
