package com.example.bankingapp.controller;

import com.example.bankingapp.dto.*;
import com.example.bankingapp.entity.Account;
import com.example.bankingapp.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService service;

    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @ModelAttribute AccountCreateRequest req) {
        return ResponseEntity.ok(service.createAccount(req));
    }

    @PostMapping("/deposit")
    public ResponseEntity<AccountResponse> deposit(@Valid @RequestBody AmountRequest req) {
        return ResponseEntity.ok(service.deposit(req));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@Valid @RequestBody AmountRequest req) {
        return ResponseEntity.ok(service.withdraw(req));
    }

    @PutMapping("/{number}/close")
    public ResponseEntity<AccountResponse> close(@PathVariable String accountNumber) {
        return ResponseEntity.ok(service.closeAccount(accountNumber));
    }

    @PostMapping("/close/{accountNumber}")
    public ResponseEntity<AccountResponse> inquire(@PathVariable String accountNumber) {
        return ResponseEntity.ok(service.inquireAccount(accountNumber));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<List<TransactionResponse>> getHistory(@PathVariable String accountNumber) {
        return ResponseEntity.ok(service.getTransactionHistory(accountNumber));
    }
}
