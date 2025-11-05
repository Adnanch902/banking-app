package com.example.bankingapp.service;

import com.example.bankingapp.dto.CustomerRequest;
import com.example.bankingapp.dto.CustomerResponse;
import com.example.bankingapp.dto.CustomerWithAccountsResponse;
import com.example.bankingapp.entity.Account;
import com.example.bankingapp.entity.Customer;
import com.example.bankingapp.utils.AccountStatus;
import com.example.bankingapp.utils.AccountType;
import com.example.bankingapp.exception.ResourceNotFoundException;
import com.example.bankingapp.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private IdGeneratorService idGeneratorService;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John");
        customer.setCustomerId("CUST-2025-000001");
    }

    // ---------------------------------------------------
    //  TEST #1 — createCustomer()
    // ---------------------------------------------------
    @Test
    void testCreateCustomer() {

        CustomerRequest req = new CustomerRequest("John");

        // First save: before ID generation
        when(customerRepository.save(any())).thenReturn(customer);

        when(idGeneratorService.generateCustomerId(1L))
                .thenReturn("CUST-2025-000001");

        CustomerResponse response = customerService.createCustomer(req);

        assertNotNull(response);
        assertEquals("John", response.name());
        assertEquals("CUST-2025-000001", response.customerId());
    }

    // ---------------------------------------------------
    //  TEST #2 — inquireCustomer() (customer found)
    // ---------------------------------------------------
    @Test
    void testInquireCustomer() {

        when(customerRepository.findByCustomerId("CUST-2025-000001"))
                .thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.inquireCustomer("CUST-2025-000001");

        assertEquals("John", response.name());
        assertEquals("CUST-2025-000001", response.customerId());
    }

    // ---------------------------------------------------
    //  TEST #3 — inquireCustomer() throws not found
    // ---------------------------------------------------
    @Test
    void testInquireCustomer_NotFound() {

        when(customerRepository.findByCustomerId("BAD-ID"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> customerService.inquireCustomer("BAD-ID"));
    }

    // ---------------------------------------------------
    //  TEST #4 — findEntityByCustomerId() success
    // ---------------------------------------------------
    @Test
    void testFindEntityByCustomerId() {

        when(customerRepository.findByCustomerId("CUST-2025-000001"))
                .thenReturn(Optional.of(customer));

        Customer result = customerService.findEntityByCustomerId("CUST-2025-000001");

        assertNotNull(result);
        assertEquals("John", result.getName());
    }

    // ---------------------------------------------------
    //  TEST #5 — findEntityByCustomerId() throws exception
    // ---------------------------------------------------
    @Test
    void testFindEntityByCustomerId_NotFound() {

        when(customerRepository.findByCustomerId("UNKNOWN"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> customerService.findEntityByCustomerId("UNKNOWN"));
    }

    // ---------------------------------------------------
    //  TEST #6 — getAllCustomersWithAccounts()
    // ---------------------------------------------------
    @Test
    void testGetAllCustomersWithAccounts() {

        Customer c = new Customer();
        c.setCustomerId("CUST-2025-000001");
        c.setName("John");

        Account acc = new Account();
        acc.setAccountNumber("ACC-001");
        acc.setAccountType(AccountType.SAVINGS);
        acc.setStatus(AccountStatus.ACTIVE);
        acc.setBalance(BigDecimal.valueOf(5000.0));
        acc.setCustomer(c);

        c.setAccounts(List.of(acc));

        when(customerRepository.findAll()).thenReturn(List.of(c));

        List<CustomerWithAccountsResponse> result = customerService.getAllCustomersWithAccounts();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getName());
        assertEquals(1, result.get(0).getAccounts().size());
        assertEquals("ACC-001", result.get(0).getAccounts().get(0).accountNumber());
    }
}
