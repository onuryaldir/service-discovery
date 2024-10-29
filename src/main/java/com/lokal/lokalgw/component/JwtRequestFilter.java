package com.lokal.lokalgw.component;

import com.lokal.lokalgw.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String requestURI = request.getRequestURI();
        if ("/api/auth/login".equals(requestURI)) {
            // Bypass JWT validation for login
            chain.doFilter(request, response); // Proceed to the next filter
            return;
        }

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtService.extractUsername(jwt);
        }

        // Validate the JWT
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtService.validateToken(jwt)) {
                // Set authentication in context (if needed)
            }
        }

        chain.doFilter(request, response);
    }
}