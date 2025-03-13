package ru.mzuev.taskmanagementsystem.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Неверные email или пароль");
    }
}