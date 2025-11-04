package com.example.bankingapp.dto;

import com.example.bankingapp.utils.AccountStatus;

import java.math.BigDecimal;

public record AccountResponse(
        String accountNumber,
        String accountType,
        AccountStatus status,
        BigDecimal balance,
        String customerId,
        String customerName
) {}
