package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();

    public User(Long id, String name, String email, String login, LocalDate birthday, Set<Long> friends) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
        this.friends = friends;
    }

    public User(String name, String email, String login, LocalDate birthday, Set<Long> friends) {
        this.name = name;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
        this.friends = friends;
    }

    public User() {
    }

    public void addFriend(Long userId) {
        friends.add(userId);
    }

    public void deleteFriend(Long userId) {
        friends.remove(userId);
    }
}
