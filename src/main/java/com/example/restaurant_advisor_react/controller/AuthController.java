package com.example.restaurant_advisor_react.controller;

import com.example.restaurant_advisor_react.AuthUser;
import com.example.restaurant_advisor_react.model.dto.AuthRequest;
import com.example.restaurant_advisor_react.model.dto.JwtResponse;
import com.example.restaurant_advisor_react.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"}, allowCredentials = "true")
@RestController
@RequestMapping(path = AuthController.AUTH_URL, produces = APPLICATION_JSON_VALUE)
public class AuthController {

    static final String AUTH_URL = "/api/v1/auth";

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(), request.getPassword()
                            )
                    );

            AuthUser user = (AuthUser) authentication.getPrincipal();

            JwtResponse jwtResponse = new JwtResponse(JwtUtil.generateToken(user), "");
            return ResponseEntity.ok().body(jwtResponse);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token, @AuthenticationPrincipal AuthUser user) {
        try {
            Boolean isValidToken = JwtUtil.validateToken(token, user);
            return ResponseEntity.ok(isValidToken);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.ok(false);
        }
    }

}