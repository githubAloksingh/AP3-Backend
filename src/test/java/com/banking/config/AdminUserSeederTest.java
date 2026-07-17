package com.banking.config;

import com.banking.entity.Account;
import com.banking.entity.Customer;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;
import com.banking.repository.UserRepository;
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
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AdminUserSeeder adminUserSeeder;

    @Test
    void shouldCreateDemoAccountForAdminCustomerWhenMissing() {
        when(userRepository.existsByEmail("admin@gmail.com")).thenReturn(false);

        Customer savedCustomer = new Customer();
        savedCustomer.setEmail("admin@gmail.com");
        savedCustomer.setPhoneNumber("9999999999");
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        adminUserSeeder.run(new DefaultApplicationArguments(new String[0]));

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, org.mockito.Mockito.atLeast(2)).save(accountCaptor.capture());
        assertEquals("ACC-DEMO-001", accountCaptor.getAllValues().get(0).getAccountNumber());
    }
}
