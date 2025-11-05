package com.example.bankingapp.dto;

import com.example.bankingapp.utils.AccountType;

public record AccountCreateRequest(String customerId, AccountType accountType) {
}
