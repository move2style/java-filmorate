package ru.yandex.practicum.filmorate.model;

<<<<<<< HEAD
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
=======
import lombok.Builder;
>>>>>>> 97c2343 (скопирован мейн для группового занятия)
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
<<<<<<< HEAD
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
=======
import java.util.HashMap;
import java.util.Map;


@Data
@Builder
public class User implements Entity {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Map<Long, FriendshipStatus> friends;
>>>>>>> 97c2343 (скопирован мейн для группового занятия)

    public Map<Long, FriendshipStatus> getFriends() {
        if (friends == null) {
            friends = new HashMap<>();
        }
        return friends;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}

