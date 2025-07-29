package ru.yandex.practicum.filmorate.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, FilmDbStorage.class, GenreDbStorage.class, MpaDbStorage.class})
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;

    @BeforeAll
    static void setupDatabase(@Autowired JdbcTemplate jdbcTemplate) {
        System.out.println("Выполняется настройка базы данных ОДИН РАЗ...");

        jdbcTemplate.update("MERGE INTO genre KEY(genre_id) VALUES (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик')");
        jdbcTemplate.update("MERGE INTO mpa KEY(mpa_id) VALUES (1, 'G'), (2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17')");
        jdbcTemplate.update("MERGE INTO friendship_status KEY(friendship_status_id) VALUES (1, 'PENDING_SENT'), (2, 'FRIENDS'), (3, 'DECLINED')");
    }


    @Test
    public void testCreateAndGetUserById() {
        User newUser = User.builder()
                .email("user@example.com")
                .login("testlogin")
                .name("Test User")
                .birthday(LocalDate.of(1990, 5, 15))
                .build();
        User createdUser = userStorage.create(newUser);

        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getLogin()).isEqualTo("testlogin");

        User retrievedUser = userStorage.getOne(createdUser.getId());
        assertThat(retrievedUser)
                .isNotNull()
                .isEqualToIgnoringGivenFields(createdUser, "friends");
    }

    @Test
    public void testFindAllUsers_WhenEmpty() {
        Collection<User> users = userStorage.findAll();
        assertThat(users).isNotNull();
        assertThat(users.size()).isEqualTo(0);
    }

    @Test
    public void testFindAllUsers_WithData() {
        User user1 = User.builder().email("u1@ya.ru").login("u1").name("User One").birthday(LocalDate.now().minusYears(20)).build();
        User user2 = User.builder().email("u2@ya.ru").login("u2").name("User Two").birthday(LocalDate.now().minusYears(25)).build();
        userStorage.create(user1);
        userStorage.create(user2);

        Collection<User> users = userStorage.findAll();
        assertThat(users).isNotNull();
        assertThat(users.size()).isEqualTo(2);
        assertThat(users)
                .extracting(User::getLogin)
                .containsExactlyInAnyOrder("u1", "u2");
    }

    @Test
    public void testUpdateUser() {
        User newUser = User.builder().email("update@ya.ru").login("update_login").name("Initial Name").birthday(LocalDate.now().minusYears(30)).build();
        User createdUser = userStorage.create(newUser);

        createdUser.setName("Updated Name");
        createdUser.setLogin("updated_login_final");
        User updatedUser = userStorage.update(createdUser);

        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getLogin()).isEqualTo("updated_login_final");

        User retrievedUser = userStorage.getOne(createdUser.getId());
        assertThat(retrievedUser.getName()).isEqualTo("Updated Name");
    }

    @Test
    public void testGetUserById_NotFound() {
        assertThatThrownBy(() -> userStorage.getOne(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с ID 999 не найден");
    }

    @Test
    public void testCreateAndGetFilmById() {
        Mpa mpa = new Mpa(1L, "G");
        SortedSet<Genre> genres = new TreeSet<>();
        genres.add(new Genre(1L, "Комедия"));
        Film newFilm = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2022, 1, 1))
                .duration(120L)
                .mpa(mpa)
                .genres(genres)
                .build();
        Film createdFilm = filmStorage.create(newFilm);


        assertThat(createdFilm.getId()).isNotNull();
        assertThat(createdFilm.getName()).isEqualTo("Test Film");
        assertThat(createdFilm.getMpa().getId()).isEqualTo(1L);
        assertThat(createdFilm.getGenres())
                .extracting(Genre::getId)
                .contains(1L);


        Film retrievedFilm = filmStorage.getOne(createdFilm.getId());
        assertThat(retrievedFilm).isNotNull();
        assertThat(retrievedFilm.getName()).isEqualTo("Test Film");
        assertThat(retrievedFilm.getMpa().getId()).isEqualTo(1L);
        assertThat(retrievedFilm.getGenres())
                .extracting(Genre::getId)
                .contains(1L);
        assertThat(retrievedFilm.getLikes()).isNotNull().isEmpty();
    }


    @Test
    void testFindAllFilms_NoLikesOnCreate() {
        Mpa mpaG = new Mpa(1L, "G");

        Film film1 = Film.builder()
                .name("First Film")
                .description("Desc1")
                .releaseDate(LocalDate.of(2020, 5, 20))
                .duration(90L)
                .mpa(mpaG)
                .build();
        Film film2 = Film.builder()
                .name("Second Film")
                .description("Desc2")
                .releaseDate(LocalDate.of(2021, 6, 15))
                .duration(110L)
                .mpa(mpaG)
                .build();

        Film created1 = filmStorage.create(film1);
        Film created2 = filmStorage.create(film2);

        Collection<Film> all = filmStorage.findAll();
        assertThat(all).hasSize(2);

        Map<Long, Film> map = new HashMap<>();
        for (Film f : all) map.put(f.getId(), f);

        Film f1 = map.get(created1.getId());
        assertThat(f1.getLikes()).isEmpty();

        Film f2 = map.get(created2.getId());
        assertThat(f2.getLikes()).isEmpty();
    }

    @Test
    void testUpdateFilm_WithExistingUserLike() {
        User testUser = User.builder()
                .email("like@test.ru")
                .login("likeUser")
                .name("Лайкер")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User createdUser = userStorage.create(testUser);
        long userId = createdUser.getId();

        Mpa mpa = new Mpa(1L, "G");
        Film film = Film.builder()
                .name("Orig")
                .description("Desc")
                .releaseDate(LocalDate.of(2019, 3, 10))
                .duration(100L)
                .mpa(mpa)
                .build();
        Film created = filmStorage.create(film);

        created.setName("NewName");
        created.setDescription("NewDesc");
        created.setReleaseDate(LocalDate.of(2019, 4, 1));
        created.setDuration(150L);
        created.setMpa(new Mpa(2L, "PG-13"));
        created.setLikes(new HashSet<>(Set.of(userId)));

        Film updated = filmStorage.update(created);
        assertThat(updated.getId()).isEqualTo(created.getId());
        assertThat(updated.getName()).isEqualTo("NewName");

        Film reloaded = filmStorage.getOne(created.getId());
        assertThat(reloaded.getLikes()).containsExactly(userId);
    }

    @Test
    void testUpdateNonExistent_ThrowsNotFound() {
        Film fake = Film.builder()
                .id(9999L)
                .name("X")
                .description("Y")
                .releaseDate(LocalDate.now())
                .duration(10L)
                .mpa(new Mpa(1L, "G"))
                .build();

        assertThatThrownBy(() -> filmStorage.update(fake))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void testFindAllGenres() {
        Collection<Genre> genres = genreStorage.findAll();
        assertThat(genres).extracting(Genre::getName).contains("Комедия", "Драма");
    }

    @Test
    public void testGetGenreById() {
        Genre genre = genreStorage.getOne(1L);
        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(1L);
        assertThat(genre.getName()).isEqualTo("Комедия");
    }

    @Test
    public void testGetGenreById_NotFound() {
        assertThatThrownBy(() -> genreStorage.getOne(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void testFindAllMpa() {
        Collection<Mpa> mpaRatings = mpaStorage.findAll();
        assertThat(mpaRatings).extracting(Mpa::getName).contains("G", "PG", "PG-13");
    }

    @Test
    public void testGetMpaById() {
        Mpa mpa = mpaStorage.getOne(1L);
        assertThat(mpa).isNotNull();
        assertThat(mpa.getId()).isEqualTo(1L);
        assertThat(mpa.getName()).isEqualTo("G");
    }

    @Test
    public void testGetMpaById_NotFound() {
        assertThatThrownBy(() -> mpaStorage.getOne(999L))
                .isInstanceOf(NotFoundException.class);
    }

}
