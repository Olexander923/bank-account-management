package com.shadrin.console_commands;

import com.shadrin.entity.Account;
import com.shadrin.services.AccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

/**
 * Запрашивает ID счета и сумму. Снимает указанную сумму со счета.
 */
@Component
public class WithdrawAccountCommand implements OperationCommand {
    private static final Logger log = LogManager.getLogger(WithdrawAccountCommand.class);
    private final AccountService accountService;
    private final Scanner scanner;

    public WithdrawAccountCommand(AccountService accountService, Scanner scanner) {
        this.accountService = accountService;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        try {
            System.out.println("Enter account ID to withdraw from:");
            long accountId = Long.parseLong(scanner.nextLine());
            System.out.println("Enter amount to withdraw:");
            BigDecimal withdrawAmount = new BigDecimal(scanner.nextLine());
            accountService.withdraw(accountId, withdrawAmount);
            Account updateAccount = accountService.findAccountById(accountId)
                    .orElseThrow(() -> new IllegalStateException(
                            "Account not found after withdrawal"));
            System.out.println(String.format(
                    "Withdraw was made successfully! %n" +
                            "From account: %d%n " +
                            "Withdraw amount: %s%n" +
                            "Transfer commission: %s%n" +
                            "Renew balance: %s",
                    accountId,
                    withdrawAmount.setScale(2, RoundingMode.HALF_UP),
                    updateAccount.getTransferCommission().setScale(2, RoundingMode.HALF_UP),
                    updateAccount.getMoneyAmount().setScale(2, RoundingMode.HALF_UP)
            ));

        } catch (NumberFormatException e) {
            log.error("Error: Invalid input format for 'withdraw' operation");
            System.err.println("Error, please enter valid value");
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Operation failed: {}", e.getMessage(),e);
            System.err.println("Operation failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected system error in 'withdraw' operation",e);
            System.err.println("System error: Please contact support");
        }
    }

    @Override
    public AccountOperationType getOperationType() {
        return AccountOperationType.ACCOUNT_WITHDRAW;
    }
}
