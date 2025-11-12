package com.example.bankingapp.dto;

import lombok.Builder;

@Builder
public record LoginResponse(String token) {
}
