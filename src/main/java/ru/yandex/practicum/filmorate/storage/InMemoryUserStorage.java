package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

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
        UserService.validateUser(user);
        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User updateUser(User newUser) {

        if (users.containsKey(newUser.getId())) {
            User oldPost = users.get(newUser.getId());

            UserService.validateUser(newUser);

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
}
