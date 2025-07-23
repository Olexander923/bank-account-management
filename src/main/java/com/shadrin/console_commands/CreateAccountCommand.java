package com.shadrin.console_commands;

import com.shadrin.entity.Account;
import com.shadrin.services.AccountService;
import com.shadrin.services.UserService;
import org.springframework.stereotype.Component;
import java.util.Scanner;

/**
 * Запрашивает ID пользователя. Создает новый счет для
 * указанного пользователя. Счет создается с дефолтным балансом из настроек.
 * Назначается уникальный id аккаунту.
 */
@Component
public class CreateAccountCommand implements OperationCommand {
    private final Scanner scanner;
    private final AccountService accountService;
    private final UserService userService;

    public CreateAccountCommand(
            Scanner scanner,
            AccountService accountService,
            UserService userService) {
        this.scanner = scanner;
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public void execute() {
        try {
            System.out.println("Enter user ID:");
            long userId = Long.parseLong(scanner.nextLine());
            var user = userService.findUserById(userId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No such user with id=%s".formatted(userId)));
            Account newAccount = accountService.createAccount(user);
            user.getAccountList().add(newAccount);
            System.out.println(String.format(
                    "Account created successfully!%n " +
                            "Account ID: %d%n " +
                            "User ID: %d%n " +
                            "Initial balance: %.2f",
                    newAccount.getAccountId(),
                    newAccount.getUserId(),
                    newAccount.getMoneyAmount()
            ));

        } catch (RuntimeException e) {
            System.err.println("Error: invalid values, please check data");
        }
    }

    @Override
    public AccountOperationType getOperationType() {
        return AccountOperationType.ACCOUNT_CREATE;
    }
}
