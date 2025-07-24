package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll();

    Film addFilm(@RequestBody Film film);

    Film update(@RequestBody Film newFilm);

    Film findFilm(Long idFilm);

    void addLike(Long idFilm, Long idUser);

    long getNextId();
}
