package com.shadrin.console_commands;

import com.shadrin.entity.User;
import com.shadrin.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import java.util.Scanner;

@Component
public class CreateUserCommand implements OperationCommand {
    private static final Logger log = LogManager.getLogger(CreateUserCommand.class);
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
            log.error("Unexpected system error in 'create user' operation",e);
            System.err.println("System error: Please contact support " + e.getMessage());
        }
    }

    @Override
    public AccountOperationType getOperationType() {
        return AccountOperationType.USER_CREATE;
    }

}
