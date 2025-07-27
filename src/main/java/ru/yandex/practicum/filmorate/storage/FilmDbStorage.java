package ru.yandex.practicum.filmorate.storage;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static ru.yandex.practicum.filmorate.service.FilmService.validateFilm;
import static ru.yandex.practicum.filmorate.service.FilmService.validateFilmDependencies;
import static ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage.films;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        validateFilmDependencies(film);

        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("mpa_id", film.getMpa().getId());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");

        Long filmId = simpleJdbcInsert.executeAndReturnKey(values).longValue();
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String sqlQuery = "insert into film_genre(id_film, id_genre) " +
                        "values (?, ?)";
                jdbcTemplate.update(sqlQuery,
                        filmId,
                        genre.getId());
            }
        }
        return findFilm(filmId);
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "select f.id id, f.name name,f.description description,\n" +
                "f.mpa_id mpa_id, m.name as mpa_name,\n" +
                "f.release_date release_date, f.duration as duration\n" +
                "from films f\n" +
                "JOIN mpa m ON m.id = f.mpa_id\n";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    public Film update(@RequestBody Film film) {
        validateFilm(film);
        validateFilmDependencies(film);

        findFilm(film.getId());

        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
            String genreSqlQuery = "delete from film_genre where id_film = ?";
            jdbcTemplate.update(genreSqlQuery, film.getId());
            for (Genre genre : film.getGenres()) {
                genreSqlQuery = "insert into film_genre(id_film, id_genre) " +
                        "values (?, ?)";
                jdbcTemplate.update(genreSqlQuery,
                        film.getId(),
                        genre.getId());
            }
        }

        if (film.getLikes() != null) {
            String likeSqlQuery = "delete from likes where id_film = ?";
            jdbcTemplate.update(likeSqlQuery, film.getId());

            for (Long userId : film.getLikes()) {
                likeSqlQuery = "insert into likes(id_film, user_id) " +
                        "values (?, ?)";
                jdbcTemplate.update(likeSqlQuery,
                        film.getId(),
                        userId);
            }
        }
        return findFilm(film.getId());
    }

    public Film findFilm(Long idFilm) {
        String sql = "select f.id id, f.name name,f.description description,\n" +
                "f.mpa_id mpa_id, m.name as mpa_name,\n" +
                "f.release_date release_date, f.duration as duration\n" +
                "from films f\n" +
                "JOIN mpa m ON m.id = f.mpa_id\n" +
                "where f.id = ?";

        Collection<Film> filmCollection = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), idFilm);
        if (filmCollection.size() == 1) {
            return ((List<Film>) filmCollection).get(0);
        } else {
            throw new NotFoundException(String.format("Фильма с id-%d не существует.", idFilm));
        }
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        Integer mpaId = rs.getInt("mpa_id");
        String mpaName = rs.getString("mpa_name");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Integer duration = rs.getInt("duration");

        String genreSql = "select g.* \n" +
                "from film_genre f\n" +
                "JOIN genre g ON (f.id_genre = g.id)\n" +
                "where id_film = ?\n" +
                "ORDER BY g.id ASC";
        List<Genre> genreCollection = jdbcTemplate.query(genreSql, (rs1, rowNum) -> makeFilmsGenre(rs1), id);

        String likesSql = "select * from likes where id_film = ?";
        List<Long> usersCollection = jdbcTemplate.query(likesSql, (rs1, rowNum) -> makeFilmsLike(rs1), id);

        return new Film(id, name, description, duration, releaseDate, new HashSet<>(usersCollection),
                new LinkedHashSet<>(genreCollection), new Mpa(mpaId, mpaName));
    }

    private Genre makeFilmsGenre(ResultSet rs) throws SQLException {
        Integer genreId = rs.getInt("id");
        String genreName = rs.getString("name");
        return new Genre(genreId, genreName);
    }

    private Long makeFilmsLike(ResultSet rs) throws SQLException {
        return rs.getLong("user_id");
    }

    public boolean genreExists(int genreId) {
        String sql = "SELECT COUNT(*) FROM genre WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, genreId);
        return count != null && count > 0;
    }

    public boolean mpaExists(int mpaId) {
        String sql = "SELECT COUNT(*) FROM mpa WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, mpaId);
        return count != null && count > 0;
    }

    @Override
    public void addLike(Long idFilm, Long idUser) {
        String likeSqlQuery = "insert into likes(id_film, user_id) values (?, ?)";
        jdbcTemplate.update(likeSqlQuery, idFilm, idUser);
    }

    @Override
    public long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}