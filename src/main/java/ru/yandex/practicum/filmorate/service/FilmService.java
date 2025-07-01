package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage storage;
    private final UserStorage userStorage;

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
        log.info("Начало выполнения метода topList");
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

    public Film addFilm(Film film) {
        return storage.addFilm(film);
    }

    public Film update(Film newFilm) {
        return storage.update(newFilm);
    }

    public static Film validateFilm(Film film) {
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        LocalDate filmReleaseDate = LocalDate.parse(film.getReleaseDate());
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
}
