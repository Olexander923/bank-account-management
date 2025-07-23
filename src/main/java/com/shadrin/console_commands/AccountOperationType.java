package com.shadrin.console_commands;

/**
 * Входные типы операций, доступные для обработки через консоль
 */
public enum AccountOperationType {
    USER_CREATE,// Создание нового пользователя.
    SHOW_ALL_USERS,// Отображение списка всех пользователей.
    ACCOUNT_CREATE,//Создание нового счета для пользователя.
    ACCOUNT_CLOSE, // Закрытие счета.
    ACCOUNT_DEPOSIT,// Пополнение счета.
    ACCOUNT_TRANSFER,// Перевод средств между счетами.
    ACCOUNT_WITHDRAW // Снятие средств со счета.
}
