package com.example.bankingapp.dto;

import com.example.bankingapp.utils.AccountStatus;
import com.example.bankingapp.utils.AccountType;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AccountResponse(
        String accountNumber,
        AccountType accountType,
        AccountStatus status,
        BigDecimal balance,
        String customerId,
        String customerName
) {}
