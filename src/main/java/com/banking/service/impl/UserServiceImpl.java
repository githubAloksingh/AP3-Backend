package com.banking.service.impl;

import com.banking.dto.LoginRequest;
import com.banking.dto.LoginResponse;
import com.banking.dto.SignupRequest;
import com.banking.dto.SignupResponse;
import com.banking.dto.ResetPasswordRequest;
import com.banking.entity.Customer;
import com.banking.entity.User;
import com.banking.exception.DuplicateResourceException;
import com.banking.exception.InvalidCredentialsException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.CustomerRepository;
import com.banking.repository.UserRepository;
import com.banking.security.AuthTokenService;
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
    private final CustomerRepository customerRepository;
    private final AuthTokenService authTokenService;

    @Override
    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        // Check if the email is already registered
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new DuplicateResourceException(
                    "Email '" + signupRequest.getEmail() + "' is already registered"
            );
        }

        if (customerRepository.existsByEmail(signupRequest.getEmail())) {
            throw new DuplicateResourceException(
                    "Customer profile with email '" + signupRequest.getEmail() + "' already exists"
            );
        }

        // Create a new user entity
        User user = new User();
        user.setFullName(signupRequest.getFullName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(signupRequest.getPassword());

        Customer customer = new Customer();
        String fullName = signupRequest.getFullName().trim();
        String[] nameParts = fullName.split("\\s+", 2);
        customer.setFirstName(nameParts[0]);
        customer.setLastName(nameParts.length > 1 ? nameParts[1] : nameParts[0]);
        customer.setEmail(signupRequest.getEmail());
        customer.setCountry("INDIA");

        // Save the user and initial customer profile to the database
        userRepository.save(user);
        Customer savedCustomer = customerRepository.save(customer);

        return new SignupResponse(
                savedCustomer.getId(),
                "User registered successfully and customer profile created"
        );
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
        Customer customer = customerRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for email: " + user.getEmail()));

        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setCustomerId(customer.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setToken(authTokenService.createToken(user.getId(), customer.getId(), user.getEmail()));
        response.setMessage("Login successful");

        return response;
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        if (!user.getPassword().equals(request.getCurrentPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);
    }
}

