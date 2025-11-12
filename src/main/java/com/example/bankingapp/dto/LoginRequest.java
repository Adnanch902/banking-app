package com.example.bankingapp.dto;

import lombok.Builder;

@Builder
public record LoginRequest(String username, String password) {
}
