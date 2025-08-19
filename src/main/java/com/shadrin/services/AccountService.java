package com.shadrin.services;

import com.shadrin.console_commands.TransferAccountCommand;
import com.shadrin.entity.Account;
import com.shadrin.entity.User;
import com.shadrin.properties.AccountProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Сервис для управления счетами.
 */
@Service
public class AccountService {
    private AtomicInteger idCounter;
    private AccountProperties accountProperties;
    private final Map<Long, Account> accountsMap;
    private final static BigDecimal MAX_DEPOSIT_AMOUNT = new BigDecimal("1000000.00");


    public AccountService(AccountProperties accountProperties) {
        this.accountProperties = accountProperties;
        this.accountsMap = new ConcurrentHashMap<>();
        this.idCounter = new AtomicInteger(0);

    }

    /**
     * создание счета
     */
    public Account createAccount(User user) {

        //создаем новый счет
        Account newAccount = new Account(idCounter.incrementAndGet(), user.getId(), accountProperties.getDefaultAmount());

        //добавляем новый счет пользователю
        accountsMap.put(newAccount.getAccountId(), newAccount);
        return newAccount;
    }

    /**
     * Перевод средств между счетами
     */
    public BigDecimal transfer(long senderAccountId, long recipientAccountId, BigDecimal transferAmount) {
        if (transferAmount.signum() <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive"
                    .formatted(transferAmount));
        }
        //поиск счетов
        Account senderAccount = findAccountById(senderAccountId)
                .orElseThrow(() -> new IllegalArgumentException("No such account id=%s"
                        .formatted(senderAccountId)));

        Account recipientAccount = findAccountById(recipientAccountId)
                .orElseThrow(() -> new IllegalArgumentException("No such account id=%s"
                        .formatted(recipientAccountId)));

        //проверка достаточности средств
        if (senderAccount.getMoneyAmount().compareTo(transferAmount) < 0) {
            throw new IllegalStateException("Cannot transfer from account: id=%s, money amount=%s, attempted transfer=%s"
                    .formatted(senderAccount, senderAccount.getMoneyAmount(), transferAmount));
        }

        //рассчет комиссии за перевод
        BigDecimal commissionRate = senderAccount.getUserId() != recipientAccount.getUserId()
                ? accountProperties.getTransferCommission()
                : BigDecimal.ZERO;

        BigDecimal commissionAmount = transferAmount.multiply(commissionRate);
        BigDecimal totalAmountToDeposit = transferAmount.subtract(commissionAmount);

        //делаем перевод
        senderAccount.setMoneyAmount(senderAccount.getMoneyAmount().subtract(transferAmount));
        recipientAccount.setMoneyAmount(recipientAccount.getMoneyAmount().add(totalAmountToDeposit));

        return commissionAmount;
    }

    /**
     * вспомогательный метод для поиска счетов по ID
     */
    public Optional<Account> findAccountById(long accountId) {
        return Optional.ofNullable(accountsMap.get(accountId));
    }

    /**
     * попоплнение средств
     */
    public void deposit(long accountId, BigDecimal depositAmount) {
        var account = findAccountById(accountId);
        if (account.isEmpty()) {
            throw new IllegalArgumentException("Account with " + accountId + " does not exist");
        }

        if (depositAmount.signum() <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive." +
                    "Received: " + depositAmount);
        }

        if (depositAmount.compareTo(MAX_DEPOSIT_AMOUNT) > 0) {
            throw new IllegalStateException("Deposit amount exceeds maximum limit");
        }

        //проверка статуса счета
        if (!account.get().isActive()) {
            throw new IllegalArgumentException("Cannot deposit closed account." +
                    "Account ID: " + accountId);
        }

        //выполнение операции
        BigDecimal newBalance = account.get().getMoneyAmount().add(depositAmount);
        account.get().setMoneyAmount(newBalance);
        System.out.printf("Account %d successfully deposit: Amount %s, New balance %s%n",
                accountId,
                depositAmount.setScale(2, RoundingMode.HALF_UP),
                newBalance.setScale(2,RoundingMode.HALF_UP));
    }

    /**
     * снятие средств
     */
    public void withdraw(long accountId, BigDecimal withdrawAmount) {
        var account = findAccountById(accountId);
        if (account.isEmpty()) {
            throw new IllegalArgumentException("Account with " + accountId + " does not exist");
        }
        //проверка суммы снятия
        if (withdrawAmount.signum() <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive." +
                    "Received: " + withdrawAmount);
        }

        //проверка статуса счета
        if (!account.get().isActive()) {
            throw new IllegalArgumentException("Cannot withdraw from closed account." +
                    "Account ID: " + accountId);
        }

        //проверка достаточности средств
        if (account.get().getMoneyAmount().compareTo(withdrawAmount) < 0) {
            throw new IllegalStateException(String.format(
                    "Insufficient funds, available: %.2f, Attempted to withdraw: %.2f",
                    account.get().getMoneyAmount(),
                    withdrawAmount
            ));
        }

        //выполнение операции
        BigDecimal newBalance = account.get().getMoneyAmount().subtract(withdrawAmount);
        account.get().setMoneyAmount(newBalance);
        System.out.printf("Account %d successfully withdraw: Amount %s, New balance %s%n",
                accountId,
                withdrawAmount.setScale(2, RoundingMode.HALF_UP),
                newBalance.setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * закрытие счета с переводом остатка на первый активный счет пользователя
     */
    public Account closeAccount(long accountId) {
        var accountToClose = findAccountById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("No such account ID=%s"
                        .formatted(accountId)));

        //достаем все счета пользователя из мапы
        List<Account> accountList = getAllUsersAccounts(accountToClose.getUserId());

        //проверка статуса счета
        if (accountList.size() == 1) {
            throw new IllegalStateException("Cannot not close the only one account");
        }
        //фильтруем id чтобы он не был равен id удаляемого аккаунта
        Account accountToDeposit = accountList.stream()
                .filter(it -> it.getAccountId() != accountId)
                .findFirst()
                .orElseThrow();

        accountToDeposit.setMoneyAmount(accountToDeposit.getMoneyAmount().add(accountToClose.getMoneyAmount()));
        accountsMap.remove(accountId);

        return accountToClose;
    }

    public List<Account> getAllUsersAccounts(long userId) {
        return accountsMap.values()
                .stream()
                .filter(account -> account.getUserId() == userId)
                .toList();
    }
}
