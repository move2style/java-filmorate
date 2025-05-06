package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Начало выполнения метода getUsers");
        return users.values();
    }

    @PostMapping
    public User postUser(@RequestBody User user) {
        log.info("Начало выполнения метода addFilm");

        validateUser(user);

        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {

        if (users.containsKey(newUser.getId())) {
            User oldPost = users.get(newUser.getId());

            validateUser(newUser);

            oldPost.setEmail(newUser.getEmail());
            oldPost.setName(newUser.getName());
            oldPost.setLogin(newUser.getLogin());
            oldPost.setBirthday(newUser.getBirthday());

            return oldPost;
        }
        throw new ValidationException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    public User validateUser(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (user.getLogin() == null || user.getLogin().contains(" ") || user.getLogin().isBlank()) {
            log.info("Логин не может быть пустым");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getEmail() == null || !user.getEmail().contains("@") || user.getEmail().isBlank()) {
            log.info("Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        LocalDate birthday = LocalDate.parse(user.getBirthday(), formatter);
        Instant birthdayInstant = birthday.atStartOfDay(ZoneId.of("UTC")).toInstant();
        if (birthdayInstant.isAfter(Instant.now())) {
            log.info("Дата рождения не может быть в будущем.");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        return user;
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
