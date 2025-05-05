package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Начало выполнения метода findAll");
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("Начало выполнения метода addFilm");
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        LocalDate filmReleaseDate = LocalDate.parse(film.getReleaseDate());
        // проверяем выполнение необходимых условий
        if (film.getName() == null || film.getName().isBlank()) {
            log.info("Название пустое");
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            log.info("Описание больше 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }

        if (filmReleaseDate.isBefore(minReleaseDate)) {
            log.info("Дата релиза раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        if (film.getDuration() < 0) {
            log.info("Отрицательная продолжительность фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }

        // формируем дополнительные данные
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Начало выполнения метода update");
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        LocalDate filmReleaseDate = LocalDate.parse(newFilm.getReleaseDate());

        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() == null || newFilm.getName().isBlank()) {
                log.info("Апдейт на пустое название");
                throw new ValidationException("Название не может быть пустым");
            }

            if (newFilm.getDescription().length() > 200) {
                log.info("Апдейт на описание больше 200 символов");
                throw new ValidationException("Максимальная длина описания — 200 символов");
            }

            if (filmReleaseDate.isBefore(minReleaseDate)) {
                log.info("Апдейт на дату релиза раньше 28 декабря 1895 года");
                throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
            }

            if (newFilm.getDuration() < 0) {
                log.info("Апдейт на отрицательную продолжительность фильма");
                throw new ValidationException("Продолжительность фильма должна быть положительным числом");
            }
            // если публикация найдена и все условия соблюдены, обновляем её содержимое

            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDuration(newFilm.getDuration());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            return oldFilm;
        }
        throw new ValidationException("Пост с id = " + newFilm.getId() + " не найден");
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}