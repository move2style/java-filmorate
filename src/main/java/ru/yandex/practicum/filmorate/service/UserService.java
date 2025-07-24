package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage storage;


    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage inMemoryUserStorage) {
        this.storage = inMemoryUserStorage;
    }

    public Set<Long> addFriend(Long idUserAdding, Long idUserAdded) {
        User userAdding = storage.findUser(idUserAdding);
        User userAdded = storage.findUser(idUserAdded);

        if (userAdding == null || userAdded == null) {
            throw new NotFoundException("Один из пользователей не найден");
        }

        if (idUserAdded == idUserAdding) {
            throw new ValidationException("Нельзя добавить в друзья самого себя");
        }

        if (userAdding.getFriends() != null && userAdded.getFriends() != null) {
            if (userAdding.getFriends().contains(userAdded.getId())) {
                throw new DuplicatedDataException("Пользователь " + userAdding.getName()
                        + " уже есть в списке друзей пользователя " + userAdded.getName());
            }
        }
        userAdding.addFriend(idUserAdded);
        userAdded.addFriend(idUserAdding);

        storage.updateUser(userAdding);
        return userAdded.getFriends();
    }

    public Set<Long> deleteFriend(Long idUserDeleting, Long idUserDeleted) {
        User userDeleting = storage.findUser(idUserDeleting);
        User userDeleted = storage.findUser(idUserDeleted);

        if (userDeleting == null || userDeleted == null) {
            throw new NotFoundException("Один из пользователей не найден");
        }

        if (idUserDeleting == idUserDeleted) {
            throw new ValidationException("Нельзя удалить из друзей самого себя");
        }

        userDeleting.deleteFriend(idUserDeleted);
        userDeleted.deleteFriend(idUserDeleting);

        storage.updateUser(userDeleting);
        return userDeleting.getFriends();
    }

    public List<User> mutualFriends(Long idUserMain, Long idUserCompare) {
        User userMain = storage.findUser(idUserMain);
        User userCompare = storage.findUser(idUserCompare);

        if (userMain == null || userCompare == null) {
            throw new NotFoundException("Один из пользователей не найден");
        }

        if (idUserMain == idUserCompare) {
            throw new ValidationException("Нельзя сравнить друзей у самого себя");
        }

        Set<Long> finalResultId = userMain.getFriends().stream()
                .filter(userCompare.getFriends()::contains)
                .collect(Collectors.toSet());


        return finalResultId.stream()
                .map(id -> storage.findUser(id))
                .collect(Collectors.toList());
    }

    public Collection<User> getFriendsList(Long id) {
        User user = storage.findUser(id);

        if (user == null) {
            throw new NotFoundException("пользователь не найден");
        }

        if (user.getFriends() == null) {
            return null;
        }

        List<User> friendList = user.getFriends().stream()
                .map(friendId -> storage.findUser(friendId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return friendList;
    }

    public Collection<User> getUsers() {
        return storage.getUsers();
    }

    public User postUser(User user) {
        return storage.postUser(user);
    }

    public User updateUser(User newUser) {
        return storage.updateUser(newUser);
    }

    public static User validateUser(User user) {

        if (user.getLogin() == null || user.getLogin().contains(" ") || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getEmail() == null || !user.getEmail().contains("@") || user.getEmail().isBlank()) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        LocalDate birthday = user.getBirthday();
        Instant birthdayInstant = birthday.atStartOfDay(ZoneId.of("UTC")).toInstant();
        if (birthdayInstant.isAfter(Instant.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        return user;
    }
}
