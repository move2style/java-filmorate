package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    long id;
    String name;
    String description;
    int duration;
    String releaseDate;
    private Set<Long> likes = new HashSet<>();

    public void addLike(Long userId) {
        likes.add(userId);
    }

    public void deletelike(Long userId) {
        likes.remove(userId);
    }

    public Integer likeAmount() {
        return likes.size();
    }
}
