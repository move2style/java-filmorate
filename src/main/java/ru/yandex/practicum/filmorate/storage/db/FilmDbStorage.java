package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert filmSimpleJdbcInsert;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.filmSimpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "select f.film_id, f.name, f.description, f.release_date, f.duration, m.mpa_id, m.name as mpa_name " +
                "from film as f " +
                "left join mpa as m on m.mpa_id=f.mpa_id";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);

        Map<Long, Film> filmsMap = new HashMap<>();
        for (Film film : films) {
            filmsMap.put(film.getId(), film);
        }

        if (filmsMap.isEmpty()) {
            return Collections.emptyList();
        }

        setLikes(filmsMap);
        setGenres(filmsMap);

        return filmsMap.values();
    }

    @Override
    public Film create(Film element) {
        Map<String, Object> params = new HashMap<>();
        params.put("description", element.getDescription());
        params.put("name", element.getName());
        params.put("release_date", element.getReleaseDate());
        params.put("duration", element.getDuration());
        params.put("mpa_id", element.getMpa().getId());

        Number generatedId = filmSimpleJdbcInsert.executeAndReturnKey(params);
        element.setId(generatedId.longValue());

        element.setLikes(new HashSet<>());

        String sql = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, element.getGenres(), element.getGenres().size(),
                (ps, genre) -> {
                    ps.setLong(1, element.getId());
                    ps.setLong(2, genre.getId());
                });

        return getOne(element.getId());
    }

    @Override
    public Film update(Film newElement) {
        getOne(newElement.getId());

        String sqlQuery = "update film set " +
                "name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "where film_id = ?";
        jdbcTemplate.update(sqlQuery,
                newElement.getName(),
                newElement.getDescription(),
                newElement.getReleaseDate(),
                newElement.getDuration(),
                newElement.getMpa().getId(),
                newElement.getId());

        Set<Long> likesIds = newElement.getLikes();

        String sql = "DELETE FROM film_like WHERE film_id = ?";
        jdbcTemplate.update(sql, newElement.getId());

        if (likesIds != null && !likesIds.isEmpty()) {
            String insertSql = "insert into film_like (film_id, user_id) " +
                    "values (?, ?)";

            jdbcTemplate.batchUpdate(insertSql, likesIds, likesIds.size(), (ps, likeId) -> {
                ps.setLong(1, newElement.getId());
                ps.setLong(2, likeId);
            });
        }


        sql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, newElement.getId());

        if (newElement.getGenres() != null && !newElement.getGenres().isEmpty()) {
            Set<Genre> uniqueGenres = new HashSet<>(newElement.getGenres());
            sql = "insert into film_genre (film_id, genre_id) " +
                    "values (?, ?)";
            jdbcTemplate.batchUpdate(sql, uniqueGenres, uniqueGenres.size(), (ps, genre) -> {
                ps.setLong(1, newElement.getId());
                ps.setLong(2, genre.getId());
            });
        }

        return newElement;
    }

    @Override
    public Film getOne(Long id) {
        Film film;
        try {
            String sql = "select f.film_id, f.name, f.description, f.release_date, f.duration, m.mpa_id, m.name as mpa_name " +
                    "from film as f " +
                    "left join mpa as m on m.mpa_id=f.mpa_id " +
                    "where f.film_id = ?";

            film = jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);
        } catch (
                EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с ID " + id + " не найден.");
        }

        setLikes(film);
        setGenres(film);

        return film;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getObject("release_date", LocalDate.class))
                .duration(resultSet.getLong("duration"))
                .mpa(new Mpa(resultSet.getLong("mpa_id"), resultSet.getString("mpa_name")))
                .build();
    }

    private void setGenres(Map<Long, Film> filmsMap) {
        String allGenresSql = "SELECT genre_id, name FROM genre ORDER BY genre_id";
        List<Genre> allGenres = jdbcTemplate.query(allGenresSql, (rs, rowNum) ->
                new Genre(rs.getLong("genre_id"), rs.getString("name")));
        Map<Long, Genre> genreMap = new HashMap<>();
        for (Genre genre : allGenres) {
            genreMap.put(genre.getId(), genre);
        }

        MapSqlParameterSource filmParams = new MapSqlParameterSource()
                .addValue("ids", filmsMap.keySet());
        String sql = "select film_id, genre_id from film_genre where film_id in (:ids)";
        namedParameterJdbcTemplate.query(sql, filmParams, rs -> {
            long filmId = rs.getLong("film_id");
            long genreId = rs.getLong("genre_id");
            Film film = filmsMap.get(filmId);
            Genre genre = genreMap.get(genreId);
            if (film != null && genre != null) {
                film.getGenres().add(genre);
            }
        });
    }

    private void setGenres(Film film) {
        Map<Long, Film> genres = new HashMap<>();
        genres.put(film.getId(), film);
        setGenres(genres);
    }

    private void setLikes(Map<Long, Film> filmsMap) {
        MapSqlParameterSource filmParams = new MapSqlParameterSource()
                .addValue("ids", filmsMap.keySet());
        String sql = "select film_id, user_id from film_like where film_id in (:ids)";
        namedParameterJdbcTemplate.query(sql, filmParams, rs -> {
            long filmId = rs.getLong("film_id");
            long genreId = rs.getLong("user_id");
            Film film = filmsMap.get(filmId);
            if (film != null) {
                if (film.getLikes() == null) {
                    film.setLikes(new HashSet<>());
                }
                film.getLikes().add(genreId);
            }
        });
    }

    private void setLikes(Film film) {
        setLikes(Map.of(film.getId(), film));
    }
}
