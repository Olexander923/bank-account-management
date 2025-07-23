package com.shadrin.services;

import com.shadrin.entity.User;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Сервис для управления пользователями.
 */
@Service
public class UserService {//TODO добавить Set takenLogins?
    private final AccountService accountService;
    private final Map<Long, User> usersMap;
    private AtomicInteger idCounter;
    private static final String USERNAME_PATTERN =
            "^(?![0-9]+$)[a-zA-Z0-9]([._ -]?[a-zA-Z0-9]){1,18}[a-zA-Z0-9]$";
    private static final Pattern pattern = Pattern.compile(USERNAME_PATTERN);


    public UserService(AccountService accountService) {
        this.usersMap = new ConcurrentHashMap<>();
        this.accountService = accountService;
        //this.takenLogins = new HashSet<>();
        this.idCounter = new AtomicInteger(0);
    }

    /**
     * создание пользователя
     * @param login создание по логину
     * @return новый пользователь
     */
    public User createUser(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("Login cannot be empty");
        }

        //проверка уникальности логина
        boolean isLoginExist = usersMap.values().stream()
                .anyMatch(user -> user.getLogin().equalsIgnoreCase(login));
        if (isLoginExist) {
            throw new IllegalStateException("User with login '" + login + "' already exists");
        }

        //создание нового пользователя
        //idCounter.incrementAndGet();
        var newUser = new User(idCounter.incrementAndGet(), login, new ArrayList<>());
        usersMap.put(newUser.getId(), newUser);
        var newAccount = accountService.createAccount(newUser);
        newUser.getAccountList().add(newAccount);
        return newUser;
    }

    /**
     * поиск пользователя по ID
     * @param userId ID пользователя
     * @return список пользователей
     */
    public Optional<User> findUserById(long userId) {
        return Optional.ofNullable(usersMap.get(userId));
    }

    /**
     * метод для получения списка всех пользователей
     * @return новую сконвертированную коллецкию из мапы, возвращает всех пользователей
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(usersMap.values());
    }

    /**
     * вспомогательный метод для валидации логина
     */
    public String loginValidator(Scanner scanner) {
        while (true) {
            String login = scanner.nextLine().trim();
            Matcher matcher = pattern.matcher(login);
            try {
                if (login.isBlank()) {
                    throw new IllegalArgumentException("Login cannot be empty");
                }

                if (!matcher.matches()) {
                    throw new IllegalArgumentException("Invalid login format. " +
                            "Login must be 3-20 characters long, start and end with a letter or digit, "  +
                            "and may contain '.', '-', or '_' in the middle.");
                }
                return login;
            } catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
                System.err.println("Please try again.");
            }
        }
    }

}
