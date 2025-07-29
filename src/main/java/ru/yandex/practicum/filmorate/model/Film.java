package ru.yandex.practicum.filmorate.model;

<<<<<<< HEAD
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
=======
import lombok.Builder;
>>>>>>> 97c2343 (скопирован мейн для группового занятия)
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Film.
 */
@Data
<<<<<<< HEAD
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
=======
@Builder
public class Film implements Entity {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private Set<Long> likes;
    private Mpa mpa;
    private SortedSet<Genre> genres;
>>>>>>> 97c2343 (скопирован мейн для группового занятия)

    public Set<Long> getLikes() {
        if (likes == null) {
            likes = new HashSet<>();
        }
        return likes;
    }

    public Set<Genre> getGenres() {
        if (genres == null) {
            genres = new TreeSet<>();
        }
        return genres;
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
