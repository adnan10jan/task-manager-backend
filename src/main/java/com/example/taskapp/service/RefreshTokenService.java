package com.example.taskapp.service;

import com.example.taskapp.model.RefreshToken;
import com.example.taskapp.model.User;
import com.example.taskapp.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {

    private static final long REFRESH_TOKEN_DURATION_MS =
            7 * 24 * 60 * 60 * 1000;

    private final RefreshTokenRepository repo;

    public RefreshTokenService(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    public RefreshToken createRefreshToken(User user) {

        // ✅ Guaranteed delete first
        repo.deleteByUser(user);

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(
                Instant.now().plusMillis(REFRESH_TOKEN_DURATION_MS)
        );

        return repo.save(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            repo.delete(token);
            throw new RuntimeException("Refresh token expired");
        }
        return token;
    }

    public void deleteByUser(User user) {
        repo.deleteByUser(user);
    }
}
