package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Entity;

import java.util.Collection;

public interface BaseStorage<T extends Entity> {
    Collection<T> findAll();

    T create(T element);

    T update(T newElement);

    T getOne(Long id);
}
