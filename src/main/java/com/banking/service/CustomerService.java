package com.banking.service;

import com.banking.dto.CustomerDTO;

import java.util.List;

/**
 * Service interface for Customer operations.
 * Defines the contract for customer-related business logic.
 */
public interface CustomerService {

    /**
     * Create a new customer.
     */
    CustomerDTO createCustomer(CustomerDTO customerDTO);

    /**
     * Get a customer by their ID.
     */
    CustomerDTO getCustomerById(Long id);

    /**
     * Get all customers.
     */
    List<CustomerDTO> getAllCustomers();

    /**
     * Update an existing customer.
     */
    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);

}
