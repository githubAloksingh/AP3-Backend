package com.banking.config;

import com.banking.entity.Customer;
import com.banking.entity.Role;
import com.banking.repository.CustomerRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserSeederTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AdminUserSeeder adminUserSeeder;

    @Test
    void shouldCreateAdminCustomerWhenMissing() {
        when(customerRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.empty());
        when(customerRepository.findByEmail("admin@bank.com")).thenReturn(Optional.empty());

        Customer savedCustomer = new Customer();
        savedCustomer.setEmail("admin@bank.com");
        savedCustomer.setPassword("Admin@123");
        savedCustomer.setRole(Role.ROLE_ADMIN);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        adminUserSeeder.run(new DefaultApplicationArguments(new String[0]));

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        assertEquals("admin@bank.com", customerCaptor.getValue().getEmail());
        assertEquals(Role.ROLE_ADMIN, customerCaptor.getValue().getRole());
    }
}
