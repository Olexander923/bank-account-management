package com.shadrin.console_commands;

import com.shadrin.entity.User;
import com.shadrin.services.UserService;
import org.springframework.stereotype.Component;
import java.util.Scanner;

@Component
public class CreateUserCommand implements OperationCommand {
    private final Scanner scanner;
    private final UserService userService;


    public CreateUserCommand(
            UserService userService,
            Scanner scanner
    ) {
        this.userService = userService;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        try {
            //получение ввода
            System.out.println("Enter login for new user: ");
            String login = userService.loginValidator(scanner);

            //создание пользователя
            User newUser = userService.createUser(login);

            System.out.println("User created " + newUser);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public AccountOperationType getOperationType() {
        return AccountOperationType.USER_CREATE;
    }

}
