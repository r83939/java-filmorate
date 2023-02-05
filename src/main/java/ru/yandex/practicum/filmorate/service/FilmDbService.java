package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

@Service
public class FilmDbService {
    private final FilmDbStorage filmDbStorage;

    private final UserDbService userDbService;

    @Autowired
    public FilmDbService(FilmDbStorage filmDbStorage, UserDbService userDbService) {
        this.filmDbStorage = filmDbStorage;
        this.userDbService = userDbService;
    }
}
