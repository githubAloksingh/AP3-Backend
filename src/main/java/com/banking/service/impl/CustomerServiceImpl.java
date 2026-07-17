package com.banking.service.impl;

import com.banking.dto.CustomerDTO;
import com.banking.entity.Customer;
import com.banking.exception.ResourceNotFoundException;
import com.banking.exception.DuplicateResourceException;
import com.banking.repository.CustomerRepository;
import com.banking.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of CustomerService interface.
 * Contains the business logic for managing customers.
 */
@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {

        // Check if email already exists
        if (customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new DuplicateResourceException("Email '" + customerDTO.getEmail() + "' is already registered");
        }

        // Check if phone number already exists
        if (customerDTO.getPhoneNumber() != null
                && !customerDTO.getPhoneNumber().isBlank()
                && customerRepository.existsByPhoneNumber(customerDTO.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number '" + customerDTO.getPhoneNumber() + "' is already registered");
        }

        // Convert DTO to entity
        Customer customer = mapToEntity(customerDTO);

        // Save to database
        Customer savedCustomer = customerRepository.save(customer);

        // Convert entity back to DTO and return
        return mapToDTO(savedCustomer);
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return mapToDTO(customer);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {

        // Find existing customer
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        // Check if the new email is already taken by another customer
        if (!existingCustomer.getEmail().equals(customerDTO.getEmail())
                && customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new DuplicateResourceException("Email '" + customerDTO.getEmail() + "' is already registered");
        }

        // Check if the new phone number is already taken by another customer
        if (customerDTO.getPhoneNumber() != null
                && !customerDTO.getPhoneNumber().isBlank()
                && !Objects.equals(existingCustomer.getPhoneNumber(), customerDTO.getPhoneNumber())
                && customerRepository.existsByPhoneNumber(customerDTO.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number '" + customerDTO.getPhoneNumber() + "' is already registered");
        }

        // Update fields
        existingCustomer.setFirstName(customerDTO.getFirstName());
        existingCustomer.setLastName(customerDTO.getLastName());
        existingCustomer.setEmail(customerDTO.getEmail());
        existingCustomer.setPhoneNumber(customerDTO.getPhoneNumber());
        existingCustomer.setAddress(customerDTO.getAddress());
        existingCustomer.setCountry(normalizeCountry(customerDTO.getCountry()));

        // Save updated customer
        Customer updatedCustomer = customerRepository.save(existingCustomer);

        return mapToDTO(updatedCustomer);
    }

    /**
     * Converts a Customer entity to a CustomerDTO.
     */
    private CustomerDTO mapToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setAddress(customer.getAddress());
        dto.setCountry(customer.getCountry());
        return dto;
    }

    /**
     * Converts a CustomerDTO to a Customer entity.
     */
    private Customer mapToEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customer.setAddress(dto.getAddress());
        customer.setCountry(normalizeCountry(dto.getCountry()));
        return customer;
    }

    private String normalizeCountry(String country) {
        return country == null || country.isBlank() ? "INDIA" : country.trim().toUpperCase();
    }
}

