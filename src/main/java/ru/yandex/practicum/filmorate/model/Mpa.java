package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
<<<<<<< HEAD
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mpa {
    private Integer id;
    private String name;
}
=======
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class Mpa implements Entity {
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
}
>>>>>>> 97c2343 (скопирован мейн для группового занятия)
