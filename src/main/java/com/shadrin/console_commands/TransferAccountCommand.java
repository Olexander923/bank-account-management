package com.shadrin.console_commands;

import com.shadrin.services.AccountService;
import org.springframework.stereotype.Component;
import java.util.Scanner;

/**
 * Запрашивает ID счета отправителя, ID счета получателя и
 * сумму. Переводит средства между счетами. Перевод между счетами разных
 * пользвателей облагается комиссией.
 */
@Component
public class TransferAccountCommand implements OperationCommand {
    private final AccountService accountService;
    private final Scanner scanner;

    public TransferAccountCommand(
            AccountService accountService,
            Scanner scanner) {
        this.accountService = accountService;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        try {
            System.out.println("Enter sender account ID:");
            long senderAccountId = Long.parseLong(scanner.nextLine());
            System.out.println("Enter recipient account ID:");
            long recipientAccountID = Long.parseLong(scanner.nextLine());
            System.out.println("Enter transfer amount:");
            double transferAmount = Double.parseDouble(scanner.nextLine());

            //перевод с рассчетом комиссии
            double transferWithCommission = accountService.transfer(
                    senderAccountId, recipientAccountID, transferAmount);

            System.out.println(String.format(
                    "Transfer was made successfully! %n" +
                            "From account: %d%n" +
                            "To account: %d%n" +
                            "Transfer amount: %.2f%n" +
                            "Transfer commission: %.2f%n",
                    senderAccountId,
                    recipientAccountID,
                    transferAmount,
                    transferWithCommission

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
        return AccountOperationType.ACCOUNT_TRANSFER;
    }
}
