package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getUsers();

    User postUser(@RequestBody User user);

    User updateUser(@RequestBody User newUser);

    User findUser(Long idUser);

    // вспомогательный метод для генерации идентификатора нового поста
    long getNextId();
}
