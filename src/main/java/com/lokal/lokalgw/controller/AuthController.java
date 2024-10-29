package com.lokal.lokalgw.controller;

import com.lokal.lokalgw.model.AuthRequest;
import com.lokal.lokalgw.model.AccessTokenResponse;
import com.lokal.lokalgw.service.JwtService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private RestTemplate restTemplate; // To call the user service

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody AuthRequest authRequest) {
        // Call the user service to validate the user's credentials
        String userServiceUrl = "http://localhost:8195/api/user/validate"; // User service URL
        ResponseEntity<Boolean> response = restTemplate.postForEntity(userServiceUrl, authRequest, Boolean.class);

        if (response.getBody() != null && response.getBody()) {
            // Generate JWT token
            String accessToken = jwtService.generateAccessToken(authRequest.getEmail());
            String refreshToken = jwtService.generateRefreshToken(authRequest.getEmail());
            jwtService.storeRefreshToken(refreshToken, authRequest.getEmail());

            return ResponseEntity.ok(new AccessTokenResponse(accessToken, refreshToken)); // Return the generated JWT token
        } else {
            return ResponseEntity.status(401).body(null);
        }
    }

    @PostMapping("/getUserByToken")
    public ResponseEntity<String> getUserByToken(@RequestHeader String token) {

        // Call the user service to validate the user's credentials
        String userServiceUrl = "http://localhost:userms/api/auth/getByEmail/" + "email"; // User service URL
        ResponseEntity<String> response = restTemplate.getForEntity(userServiceUrl, String.class);

        if (Strings.isNotEmpty(response.getBody())) {
            return ResponseEntity.ok(response.getBody()); // Return the generated JWT token
        } else {
            return ResponseEntity.status(401).body("Authentication failed");
        }
    }
    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(@RequestHeader("Authorization") String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Refresh token is invalid or expired
        }

        String username = jwtService.getUsernameFromRefreshToken(refreshToken);
        String newAccessToken = jwtService.generateAccessToken(username);
        String newRefreshToken = jwtService.generateRefreshToken(username);

        // Invalidate the old refresh token and store the new one
        jwtService.invalidateRefreshToken(refreshToken);
        jwtService.storeRefreshToken(newRefreshToken, username);

        return ResponseEntity.ok(new AccessTokenResponse(newAccessToken, newRefreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String refreshToken) {
        jwtService.invalidateRefreshToken(refreshToken);
        return ResponseEntity.ok().build(); // Logout successful
    }
}


