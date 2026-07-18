package com.banking.config;

import com.banking.entity.Customer;
import com.banking.entity.Role;
import com.banking.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminUserSeeder implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "admin@bank.com";
    private static final String ADMIN_PASSWORD = "Admin@123";

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // Clean up the old admin
        customerRepository.findByEmail("admin@gmail.com").ifPresent(customerRepository::delete);

        Customer adminCustomer = customerRepository.findByEmail(ADMIN_EMAIL).orElseGet(() -> {
            Customer customer = new Customer();
            customer.setFirstName("System");
            customer.setLastName("Administrator");
            customer.setEmail(ADMIN_EMAIL);
            customer.setPhoneNumber("9999999999");
            customer.setAddress("Admin Office");
            customer.setCountry("INDIA");
            customer.setPassword(ADMIN_PASSWORD);
            customer.setRole(Role.ROLE_ADMIN);
            return customerRepository.save(customer);
        });

        // Ensure existing admin customer has a password and ROLE_ADMIN set
        boolean updated = false;
        if (adminCustomer.getPassword() == null || adminCustomer.getPassword().isBlank()) {
            adminCustomer.setPassword(ADMIN_PASSWORD);
            updated = true;
        }
        if (adminCustomer.getRole() != Role.ROLE_ADMIN) {
            adminCustomer.setRole(Role.ROLE_ADMIN);
            updated = true;
        }
        if (updated) {
            customerRepository.save(adminCustomer);
        }
    }
}
