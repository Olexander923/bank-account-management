package com.shadrin.entity;


public class Account {
    private final long accountId;//id номера счета
    private final long userId;
    private double moneyAmount;//текущий баланс счета
    private double transferCommission;

    public Account(long accountId, long userId, double moneyAmount) {
        this.accountId = accountId;
        this.userId = userId;
        this.moneyAmount = moneyAmount;

    }

    public Double getMoneyAmount() {
        return moneyAmount;
    }

    public long getAccountId() {
        return accountId;
    }

    public long getUserId() {
        return userId;
    }

    public void setMoneyAmount(double moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    public double getTransferCommission() {
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
