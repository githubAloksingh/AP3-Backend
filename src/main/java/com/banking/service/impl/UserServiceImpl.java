package com.banking.service.impl;

import com.banking.dto.LoginRequest;
import com.banking.dto.LoginResponse;
import com.banking.dto.SignupRequest;
import com.banking.entity.User;
import com.banking.exception.DuplicateResourceException;
import com.banking.exception.InvalidCredentialsException;
import com.banking.repository.UserRepository;
import com.banking.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of UserService interface.
 * Contains the business logic for user registration and authentication.
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public String signup(SignupRequest signupRequest) {

        // Check if the email is already registered
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new DuplicateResourceException(
                    "Email '" + signupRequest.getEmail() + "' is already registered"
            );
        }

        // Create a new user entity
        User user = new User();
        user.setFullName(signupRequest.getFullName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(signupRequest.getPassword());

        // Save the user to the database
        userRepository.save(user);

        return "User registered successfully";
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        // Find the user by email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Compare the provided password with the stored password
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Return user details without the password
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setMessage("Login successful");

        return response;
    }
}