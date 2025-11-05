package com.example.bankingapp.service;

import com.example.bankingapp.utils.AccountType;
import org.springframework.stereotype.Service;


import java.time.LocalDate;


@Service
public class IdGeneratorService {


    public String generateCustomerId(Long dbId) {
        String year = String.valueOf(LocalDate.now().getYear());
        return String.format("CUST-%s-%06d", year, dbId);
    }


    public String generateAccountNumber(Long dbId, AccountType accountType) {
        String bankCode = "MAY"; // change to actual bank code
        String branchCode = "001";
        int typeCode;

        if (accountType == null) {
            typeCode = 99;
        } else {
            typeCode = switch (accountType) {
                case SAVINGS -> 10;
                case CURRENT -> 20;
            };
        }
        return String.format("%s-%s-%02d-%08d", bankCode, branchCode, typeCode, dbId);
    }
}
