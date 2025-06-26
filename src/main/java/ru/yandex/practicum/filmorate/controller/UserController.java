package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Начало выполнения метода getUsers");
        return userStorage.getUsers();
    }

    @PostMapping
    public User postUser(@RequestBody User user) {
        return userStorage.postUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        return userStorage.updateUser(newUser);
    }

    //возвращаем список пользователей, являющихся его друзьями.
    @GetMapping("/{id}/friends")
    public Collection<User> friendsList(@PathVariable Long id) {
        return userService.getFriendsList(id);
    }

    //список друзей, общих с другим пользователем.
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> mutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.mutualFriends(id, otherId);
    }

    //добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public Collection<Long> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.addFriend(id, friendId);
    }

    //удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public Collection<Long> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.deleteFriend(id, friendId);
    }
}
