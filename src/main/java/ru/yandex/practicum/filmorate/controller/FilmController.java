package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final FilmStorage filmStorage;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Начало выполнения метода findAll");
        return filmStorage.findAll();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("Начало выполнения метода addFilm");
        return filmStorage.addFilm(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Начало выполнения метода update");
        return filmStorage.update(newFilm);
    }

    // пользователь ставит лайк фильму.
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    //пользователь удаляет лайк.
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
    }

    //возвращает список из первых count фильмов по количеству лайков.
    @GetMapping("/popular")
    public Collection<Film> topList(@RequestParam(defaultValue = "10") int count) {
        log.info("Начало выполнения метода topList");
        return filmService.topTenFilms(count);
    }
}