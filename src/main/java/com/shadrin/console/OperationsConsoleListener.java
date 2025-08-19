package com.shadrin.console;
import com.shadrin.console_commands.AccountOperationType;
import com.shadrin.console_commands.OperationCommand;
import com.shadrin.console_commands.WithdrawAccountCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * смотрит консольный ввод и исполняет соответствующие команды используя сервисы.
 */
@Component
public class OperationsConsoleListener implements Runnable {
    private static final Logger log = LogManager.getLogger(OperationsConsoleListener.class);
    private final Scanner scanner;
    private final Map<AccountOperationType, OperationCommand> commandMap;


    public OperationsConsoleListener(
            Scanner scanner,
            List<OperationCommand> operationCommandList) {
        //конвертируем лист м паму, где ключ AccountOperationType,
        // а значение соответствующий command
        this.scanner = scanner;
        this.commandMap = operationCommandList.stream()
                .collect(Collectors.toMap(
                        OperationCommand::getOperationType,
                        command -> command
                ));

    }


    @Override
    public void run() {
            System.out.println("\n===========================");
            System.out.println("TRANSACTIONAL APPLICATION");
            System.out.println("===========================\n");
            // Автоматический вывод всех доступных операций
            Arrays.stream(AccountOperationType.values())
                    .forEach(op -> System.out.println(op.name()));
            System.out.println("EXIT");
            System.out.print("\nPlease enter one of operation type: ");

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String inputCommandType = scanner.nextLine().trim().toUpperCase();

                    if ("EXIT".equalsIgnoreCase(inputCommandType)) {
                        System.out.println("Exiting application ...");
                        break;
                    }

                    try {
                        //преобразование enum'а к строке
                        AccountOperationType accountOperationType = AccountOperationType.valueOf(inputCommandType);
                        OperationCommand command = commandMap.get(accountOperationType);
                        if (command != null) {
                            command.execute();
                        } else {
                            log.error("A none-exist command has been entered");
                            System.err.println("No command handler for: " + accountOperationType);
                        }
                    } catch (IllegalArgumentException e) {
                        log.error("Entered invalid operation type",e);
                        System.err.println("Unknown operation type: " + inputCommandType);
                    }
                } catch (Exception e) {
                    log.error("Unexpected system error in 'operation console listener'",e);
                    System.err.println("Error : " + e.getMessage());
                }
            }
        }
    }


