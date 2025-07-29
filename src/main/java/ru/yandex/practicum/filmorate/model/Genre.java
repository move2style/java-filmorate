package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
<<<<<<< HEAD
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
    private Integer id;
    private String name;
}
=======
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Genre implements Entity, Comparable<Genre> {
    private Long id;
    private String name;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int compareTo(Genre o) {
        return Long.compare(this.id, o.id);
    }
}
>>>>>>> 97c2343 (скопирован мейн для группового занятия)
