package com.example.bankingapp.dto;

import java.math.BigDecimal;

public record AmountRequest(BigDecimal amount, String accountNumber) {
}
