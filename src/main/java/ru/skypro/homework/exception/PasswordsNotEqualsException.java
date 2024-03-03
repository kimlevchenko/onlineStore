package ru.skypro.homework.exception;

public class PasswordsNotEqualsException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Пароль из базы данных не совпадает с текущем паролем из запроса!";
    }
}