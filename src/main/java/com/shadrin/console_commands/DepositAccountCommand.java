package com.shadrin.console_commands;

import com.shadrin.entity.Account;
import com.shadrin.services.AccountService;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Запрашивает ID счета и сумму. Пополняет счет на указанную сумму
 */
@Component
public class DepositAccountCommand implements OperationCommand {
    private final AccountService accountService;
    private final Scanner scanner;
    private final Map<Long, Account> accountsMap = new ConcurrentHashMap<>();

    public DepositAccountCommand(AccountService accountService
            , Scanner scanner) {
        this.accountService = accountService;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        try {
            System.out.println("Enter account ID:");
            long accountId = Long.parseLong(scanner.nextLine());
            System.out.println("Enter deposit amount:");
            double depositAmount = Double.parseDouble(scanner.nextLine());
            accountService.deposit(accountId, depositAmount);
            Account updateAccount = accountService.findAccountById(accountId)
                    .orElseThrow(() -> new IllegalStateException("" +
                            "Account not found after deposit"));
            System.out.println(String.format(
                    "Deposit success! %n" +
                            "Account ID: %d%n" +
                            "Amount deposited: %.2f%n" +
                            "New Balance: %.2f",
                    accountId,
                    depositAmount,
                    updateAccount.getMoneyAmount()
            ));

        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter valid numbers");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Operation failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("System error: Please contact support");
        }

    }

    @Override
    public AccountOperationType getOperationType() {
        return AccountOperationType.ACCOUNT_DEPOSIT;
    }
}
