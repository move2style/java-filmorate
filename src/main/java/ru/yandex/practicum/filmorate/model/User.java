package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    long id;
    String email;
    String login;
    String name;
    String birthday;
    private Set<Long> friends = new HashSet<>();

    public void addFriend(Long userId) {
        friends.add(userId);
    }

    public void deleteFriend(Long userId) {
        friends.remove(userId);
    }
}
