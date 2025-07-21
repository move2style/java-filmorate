package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaStorage mpaStorage;

    @GetMapping
    public List<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    @GetMapping("/{id}")
    public Mpa find(@PathVariable Integer id) {
        return mpaStorage.find(id);
    }
}
