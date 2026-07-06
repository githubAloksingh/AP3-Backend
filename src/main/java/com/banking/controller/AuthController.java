package com.banking.controller;

import com.banking.dto.LoginRequest;
import com.banking.dto.LoginResponse;
import com.banking.dto.SignupRequest;
import com.banking.dto.SignupResponse;
import com.banking.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user authentication.
 * Provides endpoints for user registration (signup) and login.
 */
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * POST /auth/signup
     * Registers a new user.
     *
     * @param signupRequest the signup details (fullName, email, password)
     * @return a success message with HTTP 201 (Created)
     */
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        String message = userService.signup(signupRequest);
        SignupResponse response = new SignupResponse(message);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * POST /auth/login
     * Authenticates a user with email and password.
     *
     * @param loginRequest the login credentials (email, password)
     * @return user details with a success message and HTTP 200 (OK)
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}