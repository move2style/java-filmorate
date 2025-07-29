package ru.yandex.practicum.filmorate.service;

<<<<<<< HEAD
=======
import lombok.extern.slf4j.Slf4j;
>>>>>>> 97c2343 (скопирован мейн для группового занятия)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
<<<<<<< HEAD
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;
=======
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
>>>>>>> 97c2343 (скопирован мейн для группового занятия)

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
<<<<<<< HEAD
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.storage = filmStorage;
        this.userStorage = userStorage;
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
=======
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("genreDbStorage") GenreStorage genreStorage,
                       @Qualifier("mpaDbStorage") MpaStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
>>>>>>> 97c2343 (скопирован мейн для группового занятия)
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

<<<<<<< HEAD
    public Film find(Long id) {
        return storage.findFilm(id);
    }

    public Film addFilm(Film film) {
        return storage.addFilm(film);
=======
    public Film create(Film film) {
        validateFilmGenres(film);
        mpaStorage.getOne(film.getMpa().getId());
        return filmStorage.create(film);
>>>>>>> 97c2343 (скопирован мейн для группового занятия)
    }

    public Film update(Film newElement) {
        validateFilmGenres(newElement);
        mpaStorage.getOne(newElement.getMpa().getId());
        return filmStorage.update(newElement);
    }

<<<<<<< HEAD
    public static Film validateFilm(Film film) {
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        LocalDate filmReleaseDate = film.getReleaseDate();
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
=======
    public Film getOne(Long id) {
        return filmStorage.getOne(id);
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getOne(filmId);
        if (userStorage.getOne(userId) == null) {
            log.info("addLike - пользователь с id: {}, не найден", userId);
            throw new NotFoundException("Добавляемый пользователь не найден");
>>>>>>> 97c2343 (скопирован мейн для группового занятия)
        }

        film.getLikes().add(userId);
        filmStorage.update(film);
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.getOne(filmId);
        if (userId == null || userStorage.getOne(userId) == null) {
            log.info("deleteLike - пользователь с id: {}, не найден", userId);
            throw new NotFoundException("Удаляемый пользователь не найден");
        }
        if (!film.getLikes().contains(userId)) {
            throw new NotFoundException("Лайк от пользователя не найден");
        }
        film.getLikes().remove(userId);
        filmStorage.update(film);
    }

    private void validateFilmGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        Set<Long> existingGenreIds = genreStorage.findAll().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        for (Genre requestedGenre : film.getGenres()) {
            if (!existingGenreIds.contains(requestedGenre.getId())) {
                throw new NotFoundException("Жанр с id = " + requestedGenre.getId() + " не найден.");
            }
        }
    }

    //TODO: Сделать отдельный sql запрос
    public List<Film> getTopTen(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparing((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }

    public static void validateFilmDependencies(Film film) {
        validateGenres(film.getGenres());
        validateMpa(film.getMpa());
    }

    private static void validateGenres(Set<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
            boolean allGenresExist = genres.stream()
                    .allMatch(genre -> GenreService.genreExists(genre.getId()));
            if (!allGenresExist) {
                throw new NotFoundException("Один или несколько жанров не найдены в базе данных.");
            }
        }
    }

    private static void validateMpa(Mpa mpa) {
        if (mpa != null) {
            boolean mpaExists = MpaService.mpaExists(mpa.getId());
            if (!mpaExists) {
                throw new NotFoundException("Рейтинг MPA не найден в базе данных.");
            }
        }
    }
}
