package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;
    private static GenreStorage genreStorage; // Добавляем GenreStorage
    private static MpaStorage mpaStorage; // Добавляем MpaStorage

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       GenreStorage genreStorage,
                       MpaStorage mpaStorage) {
        this.storage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public void addLike(Long idFilm, Long idUser) {
        Film film = storage.findFilm(idFilm);
        User user = userStorage.findUser(idUser);

        if (film == null) {
            throw new NotFoundException("Не найден фильм");
        }

        if (user == null) {
            throw new NotFoundException("Не пользователь");
        }

        if (film.getLikes().contains(idUser)) {
            throw new ValidationException("Юзер " + idUser + " уже лайкал");
        }

        film.addLike(idUser);
        storage.addLike(idFilm, idUser);
    }

    public void deleteLike(Long idFilm, Long idUser) {
        Film film = storage.findFilm(idFilm);

        if (idUser == 0 || idFilm == 0 || film == null) {
            throw new NotFoundException("Не найден фильм или пользователь");
        }

        if (!film.getLikes().contains(idUser)) {
            throw new NotFoundException("Юзер " + idUser + " не найден в списке лайкнувших");
        }
        film.deletelike(idUser);
    }

    public Collection<Film> topTenFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Количество позиций в топе не может быть меньше 1");
        }

        Collection<Film> findAllFilm = storage.findAll();

        if (findAllFilm.size() < count) {

            return findAllFilm.stream()
                    .sorted(Comparator.comparing(Film::likeAmount).reversed())
                    .limit(count)
                    .collect(Collectors.toList());
        }

        return findAllFilm.stream()
                .sorted(Comparator.comparing(Film::likeAmount).reversed())
                .limit(findAllFilm.size())
                .collect(Collectors.toList());
    }

    public Collection<Film> findAll() {
        return storage.findAll();
    }

    public Film find(Long id) {
        return storage.findFilm(id);
    }

    public Film addFilm(Film film) {
        validateFilm(film);
        validateFilmDependencies(film);
        return storage.addFilm(film);
    }

    public Film update(Film newFilm) {
        validateFilm(newFilm);
        validateFilmDependencies(newFilm);
        return storage.update(newFilm);
    }

    public static Film validateFilm(Film film) {
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        LocalDate filmReleaseDate = film.getReleaseDate();
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if (filmReleaseDate.isBefore(minReleaseDate)) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        return film;
    }

    public static void validateFilmDependencies(Film film) {
        validateGenres(film.getGenres());
        validateMpa(film.getMpa());
    }

    private static void validateGenres(Set<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
            boolean allGenresExist = genres.stream()
                    .allMatch(genre -> genreExists(genre.getId()));
            if (!allGenresExist) {
                throw new NotFoundException("Один или несколько жанров не найдены в базе данных.");
            }
        }
    }

    private static void validateMpa(Mpa mpa) {
        if (mpa != null) {
            boolean mpaExists = mpaExists(mpa.getId());
            if (!mpaExists) {
                throw new NotFoundException("Рейтинг MPA не найден в базе данных.");
            }
        }
    }

    private static boolean genreExists(Integer genreId) {
        try {
            genreStorage.find(genreId);  // Используем ваш GenreStorage
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    private static boolean mpaExists(Integer mpaId) {
        try {
            mpaStorage.find(mpaId);  // Используем ваш MpaStorage
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }
}
