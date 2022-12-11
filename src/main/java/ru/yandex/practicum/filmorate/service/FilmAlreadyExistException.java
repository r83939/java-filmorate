package ru.yandex.practicum.filmorate.service;

public class FilmAlreadyExistException extends Exception {
    public FilmAlreadyExistException(String message) {
        super(message);
    }
}
