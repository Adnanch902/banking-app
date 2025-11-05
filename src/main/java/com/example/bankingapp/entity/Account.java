package com.example.bankingapp.entity;

import com.example.bankingapp.utils.AccountStatus;
import com.example.bankingapp.utils.AccountType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String accountNumber; // business-friendly account number

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType; // SAVINGS, CURRENT

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status; // Active, Closed

    @Column(nullable = false)
    private BigDecimal balance;

    @Version
    private Long version;  // Optimistic locking

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}
