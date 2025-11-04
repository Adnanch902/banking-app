package com.example.bankingapp.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CustomerWithAccountsResponse {

    private String customerId;
    private String name;
    private List<AccountResponse> accounts;

    public CustomerWithAccountsResponse(String customerId, String name, List<AccountResponse> accounts) {
        this.customerId = customerId;
        this.name = name;
        this.accounts = accounts;
    }
}
