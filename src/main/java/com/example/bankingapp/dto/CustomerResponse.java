package com.example.bankingapp.dto;

import lombok.Builder;

@Builder
public record CustomerResponse(String customerId, String name) {}
