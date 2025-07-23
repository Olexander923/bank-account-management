package com.shadrin.entity;

import java.util.List;

public class User {
    private final long id;
    private final String login;
    private final List<Account> accountList;

    public User(long id, String login, List<Account> accountList) {
        this.id = id;
        this.login = login;
        this.accountList = accountList;
    }

    public long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", account list=" + accountList +
                '}';
    }

}
