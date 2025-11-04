package com.example.bankingapp.repository;

import com.example.bankingapp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount_AccountNumberOrderByTimestampDesc(String accountNumber);
}
