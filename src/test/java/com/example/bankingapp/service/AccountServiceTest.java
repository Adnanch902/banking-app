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
import com.example.bankingapp.utils.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private IdGeneratorService idGeneratorService;

    @InjectMocks
    private AccountService accountService;

    Customer customer;
    Account account;

    @BeforeEach
    void init() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Adnan");
        customer.setCustomerId("CUST-2025-000001");

        account = new Account();
        account.setId(10L);
        account.setAccountNumber("MAY-001-10-00000001");
        account.setAccountType(AccountType.SAVINGS);
        account.setStatus(AccountStatus.ACTIVE);
        account.setBalance(BigDecimal.valueOf(2000));
        account.setCustomer(customer);
    }

    // ---------------------------------------------------
    //  TEST INQUIRE ACCOUNT (FOUND)
    // ---------------------------------------------------
    @Test
    void inquireAccount_success() {
        when(accountRepository.findByAccountNumber("ACC-001"))
                .thenReturn(Optional.of(account));

        AccountResponse res = accountService.inquireAccount("ACC-001");

        assertEquals("Adnan", res.customerName());
        assertEquals(account.getAccountType(), res.accountType());
    }

    // ---------------------------------------------------
    //  TEST INQUIRE ACCOUNT → NOT FOUND
    // ---------------------------------------------------
    @Test
    void inquireAccount_notFound() {
        when(accountRepository.findByAccountNumber("BAD"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> accountService.inquireAccount("BAD"));
    }

    // ---------------------------------------------------
    //  TEST DEPOSIT
    // ---------------------------------------------------
    @Test
    void deposit_success() {
        AmountRequest req = new AmountRequest(
                BigDecimal.valueOf(500), "MAY-001-10-00000001");

        when(accountRepository.findByAccountNumber(req.accountNumber()))
                .thenReturn(Optional.of(account));

        AccountResponse res = accountService.deposit(req);

        assertEquals(BigDecimal.valueOf(2500), res.balance());
        verify(transactionRepository, times(1)).save(any());
    }

    // ---------------------------------------------------
    //  TEST DEPOSIT → ACCOUNT CLOSED
    // ---------------------------------------------------
    @Test
    void deposit_accountClosed() {
        account.setStatus(AccountStatus.CLOSED);

        AmountRequest req = new AmountRequest(
                BigDecimal.TEN, account.getAccountNumber());

        when(accountRepository.findByAccountNumber(account.getAccountNumber()))
                .thenReturn(Optional.of(account));

        assertThrows(IllegalStateException.class,
                () -> accountService.deposit(req));
    }

    // ---------------------------------------------------
    //  TEST WITHDRAW SUCCESS
    // ---------------------------------------------------
    @Test
    void withdraw_success() {
        AmountRequest req = new AmountRequest(
                BigDecimal.valueOf(500), "MAY-001-10-00000001");

        when(accountRepository.findByAccountNumber(req.accountNumber()))
                .thenReturn(Optional.of(account));

        AccountResponse res = accountService.withdraw(req);

        assertEquals(BigDecimal.valueOf(1500), res.balance());
        verify(transactionRepository, times(1)).save(any());
    }

    // ---------------------------------------------------
    //  TEST WITHDRAW → INSUFFICIENT BALANCE
    // ---------------------------------------------------
    @Test
    void withdraw_insufficientBalance() {
        AmountRequest req = new AmountRequest(
                BigDecimal.valueOf(5000), account.getAccountNumber());

        when(accountRepository.findByAccountNumber(req.accountNumber()))
                .thenReturn(Optional.of(account));

        assertThrows(IllegalArgumentException.class,
                () -> accountService.withdraw(req));
    }

    // ---------------------------------------------------
    //  TEST CLOSE ACCOUNT
    // ---------------------------------------------------
    @Test
    void closeAccount_success() {

        when(accountRepository.findByAccountNumber(account.getAccountNumber()))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(any()))
                .thenReturn(account);

        AccountResponse res = accountService.closeAccount(account.getAccountNumber());

        assertEquals(AccountStatus.CLOSED, res.status());
        verify(accountRepository, times(1)).save(any());
    }

    // ---------------------------------------------------
    //  TEST CREATE ACCOUNT
    // ---------------------------------------------------
    @Test
    void createAccount_success() {
        AccountCreateRequest req = new AccountCreateRequest(
                "CUST-2025-000001",
                AccountType.SAVINGS
        );

        when(customerRepository.findByCustomerId("CUST-2025-000001"))
                .thenReturn(Optional.of(customer));
        when(accountRepository.save(any())).thenReturn(account);
        when(idGeneratorService.generateAccountNumber(10L, AccountType.SAVINGS))
                .thenReturn("ACC-GEN-0001");

        AccountResponse res = accountService.createAccount(req);

        assertEquals("ACC-GEN-0001", res.accountNumber());
        assertEquals("Adnan", res.customerName());
    }

    // ---------------------------------------------------
    //  TEST GET TRANSACTION HISTORY
    // ---------------------------------------------------
    @Test
    void getTransactionHistory_success() {

        Transaction tx = new Transaction();
        tx.setId(1L);
        tx.setType("DEPOSIT");
        tx.setAmount(BigDecimal.valueOf(100));
        tx.setBalanceAfter(BigDecimal.valueOf(2100));
        tx.setAccount(account);

        when(transactionRepository.findByAccount_AccountNumberOrderByTimestampDesc(
                account.getAccountNumber()
        )).thenReturn(List.of(tx));

        List<TransactionResponse> list =
                accountService.getTransactionHistory(account.getAccountNumber());

        assertEquals(1, list.size());
        assertEquals("DEPOSIT", list.get(0).type());
    }

    // ---------------------------------------------------
    //  TEST getActiveAccount() (THROWS WHEN NOT ACTIVE)
    // ---------------------------------------------------
    @Test
    void getActiveAccount_inactive() {
        account.setStatus(AccountStatus.CLOSED);

        when(accountRepository.findByAccountNumber(account.getAccountNumber()))
                .thenReturn(Optional.of(account));

        assertThrows(IllegalStateException.class,
                () -> accountService.closeAccount(account.getAccountNumber()));
    }
}
