package ru.skypro.homework.exception;

public class UserNotFoundException extends RuntimeException {

    private final String username;

    public UserNotFoundException(String username) {
        this.username = username;
    }

    @Override
    public String getMessage() {
        return String.format("User with username: '%s' not found!", username);
    }
}
