package com.example.bankingapp.controller;

import com.example.bankingapp.dto.CustomerRequest;
import com.example.bankingapp.dto.CustomerResponse;
import com.example.bankingapp.dto.CustomerWithAccountsResponse;
import com.example.bankingapp.entity.Customer;
import com.example.bankingapp.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest req) {
        return ResponseEntity.ok(customerService.createCustomer(req));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> inquire(@PathVariable String customerId) {
        return ResponseEntity.ok(customerService.inquireCustomer(customerId));
    }

    @GetMapping("/all")
    public List<CustomerWithAccountsResponse> getAllCustomersWithAccounts() {
        return customerService.getAllCustomersWithAccounts();
    }


}
