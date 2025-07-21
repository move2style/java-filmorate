package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
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

    public Film() {
    }

    public void addLike(Long userId) {
        likes.add(userId);
    }

    public void deletelike(Long userId) {
        likes.remove(userId);
    }

    public Integer likeAmount() {
        return likes.size();
    }

    public Film(Long id, String name, String description, LocalDate releaseDate, Integer duration, Set<Genre> genres,
                Mpa mpa, Set<Long> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.genres = genres;
        this.mpa = mpa;
        this.likes = likes;
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void deleteGenre(Genre genre) {
        genres.remove(genre);
    }
}
