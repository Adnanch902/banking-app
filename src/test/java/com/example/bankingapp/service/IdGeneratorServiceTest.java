package com.example.bankingapp.service;


import com.example.bankingapp.utils.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class IdGeneratorServiceTest {

    private IdGeneratorService idGeneratorService;

    @BeforeEach
    void setUp() {
        idGeneratorService = new IdGeneratorService();
    }

    // ---------------------------------------------------
    //  TEST: generateCustomerId()
    // ---------------------------------------------------
    @Test
    void generateCustomerId_success() {
        Long dbId = 25L;

        String result = idGeneratorService.generateCustomerId(dbId);

        String expectedYear = String.valueOf(LocalDate.now().getYear());

        assertTrue(result.startsWith("CUST-" + expectedYear + "-"));
        assertEquals("CUST-" + expectedYear + "-000025", result);
    }

    // ---------------------------------------------------
    //  TEST: generateAccountNumber() – SAVINGS
    // ---------------------------------------------------
    @Test
    void generateAccountNumber_savings() {
        Long dbId = 1L;

        String result = idGeneratorService.generateAccountNumber(dbId, AccountType.SAVINGS);

        assertEquals("MAY-001-10-00000001", result);
    }

    // ---------------------------------------------------
    //  TEST: generateAccountNumber() – CURRENT
    // ---------------------------------------------------
    @Test
    void generateAccountNumber_current() {
        Long dbId = 123L;

        String result = idGeneratorService.generateAccountNumber(dbId, AccountType.CURRENT);

        assertEquals("MAY-001-20-00000123", result);
    }

    // ---------------------------------------------------
    //  TEST: generateAccountNumber() – UNKNOWN TYPE (default -> 99)
    // ---------------------------------------------------
    @Test
    void generateAccountNumber_unknownType() {
        Long dbId = 5L;

        // Simulate a non-handled type using null to hit default branch
        String result = idGeneratorService.generateAccountNumber(dbId, null);

        assertEquals("MAY-001-99-00000005", result);
    }
}

