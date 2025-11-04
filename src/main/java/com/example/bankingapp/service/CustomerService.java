package com.example.bankingapp.service;

import com.example.bankingapp.dto.AccountResponse;
import com.example.bankingapp.dto.CustomerRequest;
import com.example.bankingapp.dto.CustomerResponse;
import com.example.bankingapp.dto.CustomerWithAccountsResponse;
import com.example.bankingapp.entity.Customer;
import com.example.bankingapp.exception.ResourceNotFoundException;
import com.example.bankingapp.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final IdGeneratorService idGeneratorService;
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    public CustomerService(CustomerRepository customerRepository, IdGeneratorService idGeneratorService) {
        this.customerRepository = customerRepository;
        this.idGeneratorService = idGeneratorService;
    }

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest req) {
        Customer customer = new Customer();
        customer.setName(req.name());
        customer = customerRepository.save(customer);
        customer.setCustomerId(idGeneratorService.generateCustomerId(customer.getId()));
        customerRepository.save(customer);
        return new CustomerResponse(customer.getCustomerId(), customer.getName());
    }

    @Cacheable(value = "customers", key = "#customerId")
    public CustomerResponse inquireCustomer(String customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
        return new CustomerResponse(customer.getCustomerId(), customer.getName());
    }

    public Customer findEntityByCustomerId(String customerId) {
        return customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    public List<CustomerWithAccountsResponse> getAllCustomersWithAccounts() {

        List<Customer> customers = customerRepository.findAll();

        return customers.stream()
                .map(c -> new CustomerWithAccountsResponse(
                        c.getCustomerId(),
                        c.getName(),
                        c.getAccounts().stream()
                                .map(acc -> new AccountResponse(
                                        acc.getAccountNumber(),
                                        acc.getAccountType(),
                                        acc.getStatus(),
                                        acc.getBalance(),
                                        acc.getCustomer().getCustomerId(),
                                        acc.getCustomer().getName()
                                )).toList()
                )).toList();
    }
}
