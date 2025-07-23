package com.shadrin.console_commands;

import org.springframework.stereotype.Component;

@Component
public interface OperationCommand {
    void execute();

    AccountOperationType getOperationType();
}
