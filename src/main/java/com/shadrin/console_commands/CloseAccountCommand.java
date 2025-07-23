package com.shadrin.console_commands;

import com.shadrin.entity.Account;
import com.shadrin.entity.User;
import com.shadrin.services.AccountService;
import com.shadrin.services.UserService;
import org.springframework.stereotype.Component;
import java.util.Scanner;


/**
 * Запрашивает ID счета. Закрывает указанный счет, переводя
 * остаток средств на первый счет пользователя. Если у пользователя всего
 * один счет, то закрывать его нельзя.
 */
@Component
public class CloseAccountCommand implements OperationCommand {
    private final AccountService accountService;
    private final Scanner scanner;
    private final UserService userService;

    public CloseAccountCommand(
            AccountService accountService,
            Scanner scanner,
            UserService userService
    ) {
        this.accountService = accountService;
        this.scanner = scanner;
        this.userService = userService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("Enter account ID:");
            long accountId = Long.parseLong(scanner.nextLine());
            Account closedAccount = accountService.closeAccount(accountId);
            User user = userService.findUserById(closedAccount.getUserId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("No such user with ID=%s"
                                    .formatted(closedAccount.getUserId())));
            user.getAccountList().remove(closedAccount);
            System.out.println(String.format(
                    "Account closed successfully!%n " +
                            "Closed account ID: %d%n " +
                            "User ID: %d%n " +
                            "Transferred amount: " +
                            "New balance: %.2f",
                    closedAccount.getAccountId(),
                    closedAccount.getUserId(),
                    closedAccount.getTransferCommission(),
                    closedAccount.getMoneyAmount()
            ));
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid account ID number");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Operation failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("System error: Please contact support");
        }
    }

    @Override
    public AccountOperationType getOperationType() {
        return AccountOperationType.ACCOUNT_CLOSE;
    }
}
