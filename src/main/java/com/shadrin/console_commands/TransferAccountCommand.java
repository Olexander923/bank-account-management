package com.shadrin.console_commands;

import com.shadrin.services.AccountService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

/**
 * Запрашивает ID счета отправителя, ID счета получателя и
 * сумму. Перевод средств между счетами, перевод между счетами разных
 * пользвателей облагается комиссией.
 */
@Component
public class TransferAccountCommand implements OperationCommand {
    private static final Logger log = LogManager.getLogger(TransferAccountCommand.class);
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
            BigDecimal transferAmount = new BigDecimal(scanner.nextLine());

            //перевод с рассчетом комиссии
            BigDecimal transferWithCommission = accountService.transfer(
                    senderAccountId, recipientAccountID, transferAmount);

            System.out.println(String.format(
                    "Transfer was made successfully! %n" +
                            "From account: %d%n" +
                            "To account: %d%n" +
                            "Transfer amount: %s%n" +
                            "Transfer commission: %s%n",
                    senderAccountId,
                    recipientAccountID,
                    transferAmount.setScale(2, RoundingMode.HALF_UP),
                    transferWithCommission.setScale(2, RoundingMode.HALF_UP)

            ));
        } catch (NumberFormatException e) {
            log.error("Error: Invalid input format for transfer operation");
            System.err.println("Error, please enter valid value");
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Operation failed: {}", e.getMessage(),e);
            System.err.println("Operation failed: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected system error in transfer operation",e);
            System.err.println("System error: Please contact support");
        }

    }

    @Override
    public AccountOperationType getOperationType() {
        return AccountOperationType.ACCOUNT_TRANSFER;
    }
}
