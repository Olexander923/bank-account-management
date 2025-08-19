package com.shadrin.entity;


import java.math.BigDecimal;

public class Account {
    private final long accountId;//id номера счета
    private final long userId;
    private BigDecimal moneyAmount;//текущий баланс счета
    private BigDecimal transferCommission;

    public Account(long accountId, long userId, BigDecimal moneyAmount) {
        this.accountId = accountId;
        this.userId = userId;
        this.moneyAmount = moneyAmount;

    }

    public BigDecimal getMoneyAmount() {
        return moneyAmount;
    }

    public long getAccountId() {
        return accountId;
    }

    public long getUserId() {
        return userId;
    }

    public void setMoneyAmount(BigDecimal moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    public BigDecimal getTransferCommission() {
        return transferCommission;
    }

    public boolean isActive() {
        return true;
    }

    @Override
    public String toString() {
        return "Account{" +
                "money amount=" + moneyAmount +
                ", user ID=" + userId +
                ", account ID=" + accountId +
                '}';
    }
}
