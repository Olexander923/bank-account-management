package com.shadrin.console_commands;

import com.shadrin.entity.Account;
import com.shadrin.services.AccountService;
import org.springframework.stereotype.Component;
import java.util.Scanner;

/**
 * Запрашивает ID счета и сумму. Снимает указанную сумму со счета.
 */
@Component
public class WithdrawAccountCommand implements OperationCommand {
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
            double withdrawAmount = Double.parseDouble(scanner.nextLine());
            accountService.withdraw(accountId, withdrawAmount);
            Account updateAccount = accountService.findAccountById(accountId)
                    .orElseThrow(() -> new IllegalStateException(
                            "Account not found after withdrawal"));
            System.out.println(String.format(
                    "Withdraw was made successfully! %n" +
                            "From account: %d%n " +
                            "Withdraw amount: %.2f%n" +
                            "Transfer commission: %.2f%n" +
                            "Renew balance: %.2f",
                    accountId,
                    withdrawAmount,
                    updateAccount.getTransferCommission(),
                    updateAccount.getMoneyAmount()
            ));

        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid input format");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Operation failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("System error: Please contact support");
        }
    }

    @Override
    public AccountOperationType getOperationType() {
        return AccountOperationType.ACCOUNT_WITHDRAW;
    }
}
