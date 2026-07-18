package com.banking.config;

import com.banking.entity.Customer;
import com.banking.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class AdminUserSeeder implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_FULL_NAME = "System Administrator";

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Customer adminCustomer = customerRepository.findByEmail(ADMIN_EMAIL).orElseGet(() -> {
            Customer customer = new Customer();
            customer.setFirstName("System");
            customer.setLastName("Administrator");
            customer.setEmail(ADMIN_EMAIL);
            customer.setPhoneNumber("9999999999");
            customer.setAddress("Admin Office");
            customer.setCountry("INDIA");
            customer.setPassword(ADMIN_PASSWORD);
            return customerRepository.save(customer);
        });

        // Ensure existing admin customer has a password set
        if (adminCustomer.getPassword() == null || adminCustomer.getPassword().isBlank()) {
            adminCustomer.setPassword(ADMIN_PASSWORD);
            customerRepository.save(adminCustomer);
        }

        // Demo accounts removed for production-ready setup
    }

}
