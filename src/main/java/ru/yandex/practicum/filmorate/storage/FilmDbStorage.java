package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "INSERT INTO FILMS (name, description, releaseDate, duration, mpa) " +
                "values (?, ?, ?, ?, ?)";
        if (jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa()) > 0) {
            return film;
        }
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE FILMS SET name=?, description=?, releaseDate=?, duration=?, mpa=?";
        if (jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa()) > 0) {
            return film;
        }
        return null;
    }

    @Override
    public Optional<Film> deleteFilm(long id) {
        Film film = getFilmById(id);
        if (film != null) {
            String sqlQuery = "DELETE FROM FILMS WHERE film_id = ?";
            if (jdbcTemplate.update(sqlQuery, id) > 0) {
                return Optional.of(film);
            }
        }
        return null;
    }

    public Film getFilmById(long id) {
        String sqlQuery = "SELECT * FROM FILMS WHERE film_id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (filmRows.next()) {
            log.info("Найден фильм: {} {}", filmRows.getString("film_id"), filmRows.getString("name"));
            Film film = new Film(
                    filmRows.getLong("user_id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    filmRows.getString("mpa"),
                    getGenres(id),
                    getLikes(id));
            return film;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return null;
        }
    }

    public Set<Long> getGenres(long filmId) {
        String sqlQuery = "SELECT DISTINCT genre_id FROM GENRES WHERE film_id = ?";
        return new HashSet<Long>(jdbcTemplate.queryForList(sqlQuery, Long.class, filmId));
    }

    public Set<Long> getLikes(long filmId) {
        String sqlQuery = "SELECT DISTINCT user_id FROM LIKES WHERE film_id = ?";
        return new HashSet<Long>(jdbcTemplate.queryForList(sqlQuery, Long.class, filmId));
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT * FROM FILMS";
        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) ->
                        new Film(
                                rs.getLong("user_id"),
                                rs.getString("name"),
                                rs.getString("description"),
                                rs.getDate("release_date").toLocalDate(),
                                rs.getInt("duration"),
                                rs.getString("mpa"),
                                getGenres(rs.getLong("user_id")),
                                getLikes(rs.getLong("user_id"))));
    }
}
