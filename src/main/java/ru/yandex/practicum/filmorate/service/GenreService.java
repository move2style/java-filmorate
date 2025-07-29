package ru.yandex.practicum.filmorate.service;

<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
public class GenreService {
    private static GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Genre find(Integer id) {
        genreExists(id);
        return genreStorage.find(id);
    }

    static boolean genreExists(Integer genreId) {
        Genre genre = genreStorage.find(genreId);

        if (genre == null) {
            throw new NotFoundException(String.format("genre с id-%d не существует.", genreId));
        }
        return true;
=======
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;


@Service
public class GenreService {
    private final GenreStorage genreStorage;

    public GenreService(@Qualifier("genreDbStorage") GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Collection<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Genre getOne(Long id) {
        return genreStorage.getOne(id);
>>>>>>> 97c2343 (скопирован мейн для группового занятия)
    }
}
