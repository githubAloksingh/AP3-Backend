package com.banking.config;

import com.banking.entity.Account;
import com.banking.entity.Customer;
import com.banking.entity.User;
import com.banking.repository.AccountRepository;
import com.banking.repository.CustomerRepository;
import com.banking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AdminUserSeeder implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_FULL_NAME = "System Administrator";
    private static final String DEMO_SENDER_ACCOUNT_NUMBER = "ACC-DEMO-001";
    private static final String DEMO_RECEIVER_EMAIL = "receiver@gmail.com";
    private static final String DEMO_RECEIVER_PHONE = "8888888888";
    private static final String DEMO_RECEIVER_ACCOUNT_NUMBER = "ACC-DEMO-002";

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByEmail(ADMIN_EMAIL)) {
            User adminUser = new User();
            adminUser.setFullName(ADMIN_FULL_NAME);
            adminUser.setEmail(ADMIN_EMAIL);
            adminUser.setPassword(ADMIN_PASSWORD);
            userRepository.save(adminUser);
        }

        Customer adminCustomer = customerRepository.findByEmail(ADMIN_EMAIL).orElseGet(() -> {
            Customer customer = new Customer();
            customer.setFirstName("System");
            customer.setLastName("Administrator");
            customer.setEmail(ADMIN_EMAIL);
            customer.setPhoneNumber("9999999999");
            customer.setAddress("Admin Office");
            customer.setCountry("INDIA");
            return customerRepository.save(customer);
        });

        createAccountIfMissing(adminCustomer, DEMO_SENDER_ACCOUNT_NUMBER, Account.AccountType.SAVINGS, new BigDecimal("10000.00"));

        Customer receiverCustomer = customerRepository.findByEmail(DEMO_RECEIVER_EMAIL).orElseGet(() -> {
            Customer customer = new Customer();
            customer.setFirstName("Demo");
            customer.setLastName("Receiver");
            customer.setEmail(DEMO_RECEIVER_EMAIL);
            customer.setPhoneNumber(DEMO_RECEIVER_PHONE);
            customer.setAddress("Demo Receiver Address");
            customer.setCountry("INDIA");
            return customerRepository.save(customer);
        });

        createAccountIfMissing(receiverCustomer, DEMO_RECEIVER_ACCOUNT_NUMBER, Account.AccountType.SAVINGS, new BigDecimal("5000.00"));
    }

    private void createAccountIfMissing(Customer customer, String accountNumber, Account.AccountType accountType, BigDecimal balance) {
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            return;
        }

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType);
        account.setBalance(balance);
        account.setCustomer(customer);
        accountRepository.save(account);
    }
}
