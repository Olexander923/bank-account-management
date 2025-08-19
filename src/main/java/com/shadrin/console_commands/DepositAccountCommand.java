package com.shadrin.console_commands;

import com.shadrin.entity.Account;
import com.shadrin.services.AccountService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Запрашивает ID счета и сумму. Пополняет счет на указанную сумму
 */
@Component
public class DepositAccountCommand implements OperationCommand {
    private static final Logger log = LogManager.getLogger(DepositAccountCommand.class);
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
            BigDecimal depositAmount = new BigDecimal(scanner.nextLine());
            accountService.deposit(accountId, depositAmount);
            Account updateAccount = accountService.findAccountById(accountId)
                    .orElseThrow(() -> new IllegalStateException("" +
                            "Account not found after deposit"));
            System.out.println(String.format(
                    "Deposit success! %n" +
                            "Account ID: %d%n" +
                            "Amount deposited: %s%n" +
                            "New Balance: %s",
                    accountId,
                    depositAmount.setScale(2, RoundingMode.HALF_UP),
                    updateAccount.getMoneyAmount().setScale(2,RoundingMode.HALF_UP)
            ));

        } catch (NumberFormatException e) {
            log.error("Error: Invalid input format for 'deposit' operation");
            System.err.println("Error, please enter valid value");
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Operation failed: {}", e.getMessage(),e);
            System.err.println("Operation failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected system error in 'deposit' operation",e);
            System.err.println("System error: Please contact support");
        }

    }

    @Override
    public AccountOperationType getOperationType() {
        return AccountOperationType.ACCOUNT_DEPOSIT;
    }
}
