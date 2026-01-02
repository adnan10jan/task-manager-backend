package com.example.taskapp.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMs;

    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {

        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    // ✅ Generate token with roles
    public String generateToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Extract username
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // ✅ Validate token (USED BY JwtAuthFilter)
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername())
                    && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ Expiration check
    public boolean isTokenExpired(String token) {
        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // ✅ Claims extractor (THIS WAS BROKEN BEFORE)
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)   // 🔥 CORRECT KEY
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}


//package com.example.taskapp.security;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import java.security.Key;
//import java.util.Date;
//import java.util.List;
//
//
//@Component
//public class JwtUtil {
//
//    private final Key key;
//    private final long expirationMs;
//
//    public JwtUtil(
//            @Value("${app.jwt.secret}") String secret,
//            @Value("${app.jwt.expiration-ms}") long expirationMs) {
//
//        this.key = Keys.hmacShaKeyFor(secret.getBytes());
//        this.expirationMs = expirationMs;
//    }
//
//
//    public boolean validateToken(String token, UserDetails userDetails) {
//        try {
//            String username = extractUsername(token);
//            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//
////    public String generateToken(String username) {
////        return Jwts.builder()
////                .setSubject(username)
////                .setIssuedAt(new Date())
////                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
////                .signWith(key, SignatureAlgorithm.HS256)
////                .compact();
////    }
//
//    public String generateToken(String username, List<String> roles) {
//        return Jwts.builder()
//                .setSubject(username)
//                .claim("roles", roles)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//
//    public boolean isTokenExpired(String token) {
//        return extractClaims(token).getExpiration().before(new Date());
//    }
//
//    private Claims extractClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(String.valueOf(EventWriterKey.getKey()))
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//
//    public String extractUsername(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
//}
