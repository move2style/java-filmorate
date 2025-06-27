package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Начало выполнения метода findAll");
        return filmService.findAll();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("Начало выполнения метода addFilm");
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Начало выполнения метода update");
        return filmService.update(newFilm);
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