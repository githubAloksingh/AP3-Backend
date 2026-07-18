package com.banking.service.impl;

import com.banking.dto.LoginRequest;
import com.banking.dto.LoginResponse;
import com.banking.dto.SignupRequest;
import com.banking.dto.SignupResponse;
import com.banking.dto.ResetPasswordRequest;
import com.banking.entity.Customer;
import com.banking.exception.DuplicateResourceException;
import com.banking.exception.InvalidCredentialsException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.CustomerRepository;
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

    private final CustomerRepository customerRepository;
    private final AuthTokenService authTokenService;

    @Override
    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        if (customerRepository.existsByEmail(signupRequest.getEmail())) {
            throw new DuplicateResourceException(
                    "Customer profile with email '" + signupRequest.getEmail() + "' already exists"
            );
        }
        Customer customer = new Customer();
        String fullName = signupRequest.getFullName().trim();
        String[] nameParts = fullName.split("\\s+", 2);
        customer.setFirstName(nameParts[0]);
        customer.setLastName(nameParts.length > 1 ? nameParts[1] : nameParts[0]);
        customer.setEmail(signupRequest.getEmail());
        customer.setCountry("INDIA");
        customer.setPassword(signupRequest.getPassword());

        Customer savedCustomer = customerRepository.save(customer);

        return new SignupResponse(
                savedCustomer.getId(),
                "User registered successfully and customer profile created"
        );
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        // Find the customer by email
        Customer customer = customerRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!customer.getPassword().equals(loginRequest.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        LoginResponse response = new LoginResponse();
        response.setId(customer.getId());
        response.setCustomerId(customer.getId());
        response.setFullName(customer.getFirstName() + " " + customer.getLastName());
        response.setEmail(customer.getEmail());
        response.setToken(authTokenService.createToken(customer.getId(), customer.getId(), customer.getEmail()));
        response.setMessage("Login successful");

        return response;
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + request.getEmail()));

        if (!customer.getPassword().equals(request.getCurrentPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        customer.setPassword(request.getNewPassword());
        customerRepository.save(customer);
    }
}

