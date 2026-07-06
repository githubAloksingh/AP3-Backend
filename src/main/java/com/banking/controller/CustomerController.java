package com.banking.controller;

import com.banking.dto.CustomerDTO;
import com.banking.service.CustomerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing customers.
 * Provides CRUD endpoints for the Customer entity.
 */
@RestController
@RequestMapping("/api/customers")
@AllArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * GET /api/customers
     * Retrieves all customers.
     *
     * @return list of all customers with HTTP 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    /**
     * GET /api/customers/{id}
     * Retrieves a customer by their ID.
     *
     * 
     * 
     * @param id the customer ID
     * @return the customer with HTTP 200 (OK)
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        CustomerDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    /**
     * PUT /api/customers/{id}
     * Updates an existing customer.
     *
     * @param id the customer ID
     * @param customerDTO the updated customer data
     * @return the updated customer with HTTP 200 (OK)
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerDTO customerDTO) {
        CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
        return ResponseEntity.ok(updatedCustomer);
    }

}
