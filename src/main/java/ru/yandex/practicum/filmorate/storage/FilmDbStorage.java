package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.util.*;

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
                    filmRows.getLong("film_id"),
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
                                rs.getLong("film_id"),
                                rs.getString("name"),
                                rs.getString("description"),
                                rs.getDate("release_date").toLocalDate(),
                                rs.getInt("duration"),
                                getMpaByFilmId(rs.getLong("film_id")),
                                getGenres(rs.getLong("user_id")),
                                getLikes(rs.getLong("user_id"))));
    }

    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM GENRES";
        return new ArrayList<Genre>(jdbcTemplate.queryForList(sqlQuery, Genre.class));
    }

    public List<Mpa> getAllMpa() {
        String sqlQuery = "SELECT * FROM MPA";
        return new ArrayList<Mpa>(jdbcTemplate.queryForList(sqlQuery, Mpa.class));
    }

    public Mpa getMpaById(long id) {
        String sqlQuery = "SELECT * FROM MPA WHERE mpa_id=?";
        return jdbcTemplate.queryForObject(sqlQuery, Mpa.class, id);
    }

    public Genre getGenreById(long id) {
        String sqlQuery = "SELECT * FROM GENRES WHERE genre_id=?";
        return jdbcTemplate.queryForObject(sqlQuery, Genre.class, id);
    }

    public boolean isFilmExist(Long filmId) {
        String sqlQuery = "SELECT 1 FROM FILMS WHERE film_id=?";
        return Boolean.TRUE.equals(jdbcTemplate.query(sqlQuery,
                (ResultSet rs) -> {
                    if (rs.next()) {
                        return true;
                    }
                    return false;
                }, filmId
        ));
    }

    public boolean addLike(Long filmId, Long userId) {
        String sqlQuery = "INSERT INTO LIKES (film_id, user_id) values (?,?)";
        return jdbcTemplate.update(sqlQuery,
                userId,
                filmId) > 0;
    }

    public boolean deleteLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE film_id = ? AND user_id = ?";
        return jdbcTemplate.update(sqlQuery, filmId, userId ) > 0;
    }

    public List<Film> getTopFilms(Integer count) {
        String sqlQuery = "SELECT * FROM FILMS WHERE film_id IN (SELECT film_id FROM (SELECT film_id, count (*)  count FROM LIKES GROUP BY film_id ORDER BY count  DESC LIMIT ?))";
        return jdbcTemplate.query(sqlQuery,
                (rs, rowNum) ->
                        new Film(
                                rs.getLong("film_id"),
                                rs.getString("name"),
                                rs.getString("description"),
                                rs.getDate("release_date").toLocalDate(),
                                rs.getInt("duration"),
                                getMpaByFilmId(rs.getLong("film_id")),
                                getGenres(rs.getLong("user_id")),
                                getLikes(rs.getLong("user_id"))),
                count );
    }

    public String getMpaByFilmId(Long filmId) {
        String sqlQuery = "SELECT name FROM mpa WHERE mpa_id = (SELECT mpa_id FROM FILMMPA WHERE film_id=?)";
        return jdbcTemplate.queryForObject(sqlQuery, String.class, filmId);
    }
}
