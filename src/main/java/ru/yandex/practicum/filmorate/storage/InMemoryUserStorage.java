package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User postUser(User user) {
        validateUser(user);
        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User updateUser(User newUser) {

        if (users.containsKey(newUser.getId())) {
            User oldPost = users.get(newUser.getId());

            validateUser(newUser);

            oldPost.setEmail(newUser.getEmail());
            oldPost.setName(newUser.getName());
            oldPost.setLogin(newUser.getLogin());
            oldPost.setBirthday(newUser.getBirthday());

            return oldPost;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @Override
    public User findUser(Long idUser) {
        User user = users.get(idUser);
        return user;
    }

    // вспомогательный метод для генерации идентификатора нового поста
    @Override
    public long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public User validateUser(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (user.getLogin() == null || user.getLogin().contains(" ") || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getEmail() == null || !user.getEmail().contains("@") || user.getEmail().isBlank()) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        LocalDate birthday = LocalDate.parse(user.getBirthday(), formatter);
        Instant birthdayInstant = birthday.atStartOfDay(ZoneId.of("UTC")).toInstant();
        if (birthdayInstant.isAfter(Instant.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        return user;
    }
}
