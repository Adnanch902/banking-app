package com.example.bankingapp.service;

import com.example.bankingapp.dto.*;
import com.example.bankingapp.entity.Account;
import com.example.bankingapp.entity.Customer;
import com.example.bankingapp.entity.Transaction;
import com.example.bankingapp.exception.ResourceNotFoundException;
import com.example.bankingapp.repository.AccountRepository;
import com.example.bankingapp.repository.CustomerRepository;
import com.example.bankingapp.repository.TransactionRepository;
import com.example.bankingapp.utils.AccountStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
//@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final IdGeneratorService idGeneratorService;
    private final TransactionRepository transactionRepository;
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository, IdGeneratorService idGeneratorService, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.idGeneratorService = idGeneratorService;
        this.transactionRepository = transactionRepository;
    }

    @Cacheable(value = "accounts", key = "#accountNumber")
    public AccountResponse inquireAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
        return toDto(account);
    }

    @CachePut(value = "accounts", key = "#req.accountNumber()")
    @Transactional
    public AccountResponse deposit(AmountRequest req) {

        Account account = getActiveAccount(req.accountNumber());
        if (account.getStatus() == AccountStatus.CLOSED) throw new IllegalArgumentException("Account is closed");
        account.setBalance(account.getBalance().add(req.amount()));
        accountRepository.save(account);

        createTransaction(account, "DEPOSIT", req.amount(), account.getBalance());
        log.info("Deposited {} to account {}", req.amount(), req.accountNumber());
        return toDto(account);
    }

    @CachePut(value = "accounts", key = "#req.accountNumber()")
    @Transactional
    public AccountResponse withdraw(AmountRequest req) {
        Account account = getActiveAccount(req.accountNumber());
        if (account.getStatus() == AccountStatus.CLOSED)  throw new IllegalArgumentException("Account is closed");
        if (account.getBalance().compareTo(req.amount()) < 0) throw new IllegalArgumentException("Insufficient balance");
        account.setBalance(account.getBalance().subtract(req.amount()));
        accountRepository.save(account);

        createTransaction(account, "WITHDRAW", req.amount(), account.getBalance());
        log.info("Withdrew {} from account {}", req.amount(), req.accountNumber());
        return toDto(account);
    }

    @CachePut(value = "accounts", key = "#accountNumber")
    @Transactional
    public AccountResponse closeAccount(String accountNumber) {
        Account account = getActiveAccount(accountNumber);
        account.setStatus(AccountStatus.CLOSED);
        account = accountRepository.save(account);
        log.info("Closed account {}", accountNumber);
        return toDto(account);
    }

    @Transactional
    public AccountResponse createAccount(AccountCreateRequest req) {
        Customer customer = customerRepository.findByCustomerId(req.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + req.customerId()));
        Account account = new Account();
        account.setAccountType(req.accountType().toUpperCase());
        account.setCustomer(customer);
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(BigDecimal.ONE);
        account = accountRepository.save(account);
        account.setAccountNumber(idGeneratorService.generateAccountNumber(account.getId(), account.getAccountType()));
        log.info("Created account {} for customer {}", account.getAccountNumber(), req.customerId());
        accountRepository.save(account);
        return toDto(account);
    }

    @Cacheable(value = "transactions", key = "#accountNumber")
    public List<TransactionResponse> getTransactionHistory(String accountNumber) {
        return transactionRepository.findByAccount_AccountNumberOrderByTimestampDesc(accountNumber)
                .stream()
                .map(this::toTransactionDto)
                .toList();
    }

    private void createTransaction(Account account, String type, BigDecimal amount, BigDecimal balanceAfter) {
        Transaction tx = new Transaction();
        tx.setType(type);
        tx.setAmount(amount);
        tx.setBalanceAfter(balanceAfter);
        tx.setAccount(account);
        transactionRepository.save(tx);
    }

    private Account getActiveAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
        if (!AccountStatus.ACTIVE.equals(account.getStatus())) {
            throw new IllegalStateException("Account is not active");
        }
        return account;
    }

    private AccountResponse toDto(Account a) {
        return new AccountResponse(
                a.getAccountNumber(), a.getAccountType(),  a.getStatus(),
                a.getBalance(), a.getCustomer().getCustomerId(), a.getCustomer().getName()
        );
    }

    private TransactionResponse toTransactionDto(Transaction t) {
        return new TransactionResponse(
                t.getId(), t.getType(), t.getAmount(), t.getBalanceAfter(), t.getTimestamp()
        );
    }
}
