package com.shadrin.console_commands;

import com.shadrin.entity.Account;
import com.shadrin.entity.User;
import com.shadrin.services.UserService;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Не требует входных данных. Выводит список всех пользователей и данные об их аккаунтах.
 */
@Component
public class ShowAllUsersCommand implements OperationCommand {
    private final UserService userService;


    public ShowAllUsersCommand(UserService userService) {
        this.userService = userService;
    }


    @Override
    public AccountOperationType getOperationType() {
        return AccountOperationType.SHOW_ALL_USERS;
    }


    @Override
    public void execute() {
        try {
            List<User> users = userService.getAllUsers();

            if (users.isEmpty()) {
                System.out.println("No users registered in the system");
                return;
            }
            System.out.println("====== USERS AND ACCOUNTS ======");

            for (User user : users) {
                System.out.println(String.format(
                        "User ID: %d | Login %s,",
                        user.getId(),
                        user.getLogin()
                ));


                List<Account> accounts = user.getAccountList();
                if (accounts.isEmpty()) {
                    System.out.println("No accounts");
                } else {
                    System.out.println("Accounts: ");
                    for (Account acc : accounts) {
                        System.out.println(String.format(
                                " - ID: %d | Balance: %.2f | %s",
                                acc.getAccountId(),
                                acc.getMoneyAmount(),
                                acc.isActive() ? "Active" : "Closed"
                        ));
                    }
                }
                System.out.println("--------------------------------------------");
            }


            //статистика
            System.out.println(String.format(
                    "TOTAL: %d users, %d accounts",
                    users.size(),
                    users.stream().mapToInt(user -> user.getAccountList().size()).sum()
            ));

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

    }
}


