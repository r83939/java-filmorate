package ru.yandex.practicum.filmorate.exception;

public class UnknownUserException extends Exception {
    public UnknownUserException(String message) {
        super(message);
    }
}
