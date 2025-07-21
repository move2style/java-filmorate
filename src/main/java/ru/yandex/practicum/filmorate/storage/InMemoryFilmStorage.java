package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Deprecated
@Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    static final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        FilmService.validateFilm(film);
        // формируем дополнительные данные
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(@RequestBody Film newFilm) {

        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            FilmService.validateFilm(newFilm);
            // если публикация найдена и все условия соблюдены, обновляем её содержимое

            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDuration(newFilm.getDuration());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            return oldFilm;
        }
        throw new NotFoundException("Пост с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public Film findFilm(Long idFilm) {
        Film film = films.get(idFilm);
        return film;
    }

    @Override
    public void addLike(Long idFilm, Long idUser) {

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
