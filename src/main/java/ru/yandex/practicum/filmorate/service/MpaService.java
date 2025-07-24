package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
public class MpaService {
    private static MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    public Mpa find(Integer id) {
        mpaExists(id);
        return mpaStorage.find(id);
    }

    static boolean mpaExists(Integer mpaId) {
        Mpa mpa = mpaStorage.find(mpaId);

        if (mpa == null) {
            throw new NotFoundException(String.format("mpa с id-%d не существует.", mpaId));
        }
        return true;
    }
}
