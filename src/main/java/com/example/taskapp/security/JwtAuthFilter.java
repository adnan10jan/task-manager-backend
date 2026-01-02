package com.example.taskapp.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsServiceImpl uds) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = uds;
    }



//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            String username = jwtUtil.extractUsername(token);
//
//            if (username != null &&
//                    SecurityContextHolder.getContext().getAuthentication() == null) {
//
//                UserDetails userDetails =
//                        userDetailsService.loadUserByUsername(username);
//
//                UsernamePasswordAuthenticationToken auth =
//                        new UsernamePasswordAuthenticationToken(
//                                userDetails, null, userDetails.getAuthorities());
//
//                auth.setDetails(
//                        new WebAuthenticationDetailsSource().buildDetails(request));
//
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }


//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        String path = request.getRequestURI();
//
//        // ✅ 1. Skip auth endpoints
//        if (path.startsWith("/api/auth")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String header = request.getHeader("Authorization");
//
//        // ✅ 2. Ignore missing or invalid header
//        if (header == null || !header.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token = header.substring(7);
//
//        // ✅ 3. Guard against bad token values
//        if (!token.contains(".")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            String username = jwtUtil.extractUsername(token);
//
//            if (username != null &&
//                    SecurityContextHolder.getContext().getAuthentication() == null) {
//
//                UserDetails userDetails =
//                        userDetailsService.loadUserByUsername(username);
//
//                if (jwtUtil.validateToken(token, userDetails)) {
//                    UsernamePasswordAuthenticationToken auth =
//                            new UsernamePasswordAuthenticationToken(
//                                    userDetails,
//                                    null,
//                                    userDetails.getAuthorities()
//                            );
//
//                    SecurityContextHolder.getContext().setAuthentication(auth);
//                }
//            }
//
//        } catch (Exception ex) {
//            // ❗ DO NOT throw — just skip auth
//        }
//
//        filterChain.doFilter(request, response);
//    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // ✅ 1. Skip auth endpoints completely
        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        // ✅ 2. Ignore missing or invalid header
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7).trim();

        // ✅ 3. Ignore malformed tokens BEFORE parsing
        if (token.isEmpty() || token.split("\\.").length != 3) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtUtil.extractUsername(token);

            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    auth.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(auth);
                }
            }
        } catch (Exception ignored) {
            // ❗ Never break the request pipeline
        }

        filterChain.doFilter(request, response);
    }
}





