package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    long id;
    @NotBlank
    private String name;
    @Size(max = 255)
    private String description;
    @Positive
    private int duration;
    private LocalDate releaseDate;
    private Set<Long> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    private Mpa mpa;

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
