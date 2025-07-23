package com.shadrin.console;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/**
 * для запуска console listener'а в отдельном потоке
 */
@Component
public class ConsoleListenerStarter {
    private final OperationsConsoleListener consoleListener;
    private Thread consoleListenerThread;

    public ConsoleListenerStarter(OperationsConsoleListener consoleListener) {
        this.consoleListener = consoleListener;
    }

    @PostConstruct
    public void postConstruct() {
        consoleListenerThread = new Thread(
                () -> {
                    consoleListener.run();
                }
        );
        consoleListenerThread.start();
    }

    @PreDestroy
    public void preDestroy() {
        consoleListenerThread.interrupt();
    }
}
