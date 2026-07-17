package com.banking.service;

import com.banking.dto.LoginRequest;
import com.banking.dto.LoginResponse;
import com.banking.dto.SignupRequest;
import com.banking.dto.SignupResponse;
import com.banking.dto.ResetPasswordRequest;

/**
 * Service interface for User authentication operations.
 * Defines the contract for signup and login business logic.
 */
public interface UserService {

    /**
     * Register a new user.
     * Checks if the email already exists before creating the user.
     *
     * @param signupRequest the signup details
     * @return signup result with the created customer profile ID
     */
    SignupResponse signup(SignupRequest signupRequest);

    /**
     * Authenticate a user with email and password.
     * Returns user details if credentials are valid.
     *
     * @param loginRequest the login credentials
     * @return user details with a success message
     */
    LoginResponse login(LoginRequest loginRequest);

    void resetPassword(ResetPasswordRequest request);
}
