package ru.yandex.practicum.filmorate.service;

<<<<<<< HEAD
=======

>>>>>>> 97c2343 (скопирован мейн для группового занятия)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

<<<<<<< HEAD
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
=======
>>>>>>> 97c2343 (скопирован мейн для группового занятия)
import java.util.Collection;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

<<<<<<< HEAD
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
=======
    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
>>>>>>> 97c2343 (скопирован мейн для группового занятия)
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newElement) {
        return userStorage.update(newElement);
    }

    public User getOne(Long id) {
        return userStorage.getOne(id);
    }

    public List<User> getFriends(Long id) {
        return userStorage.getFriends(id);
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.getOne(userId);
        userStorage.getOne(friendId);

        userStorage.addFriendship(userId, friendId, FriendshipStatus.FRIENDS);
    }

<<<<<<< HEAD
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
=======
    public void deleteFriend(Long userId, Long friendId) {
        userStorage.getOne(userId);
        userStorage.getOne(friendId);

        userStorage.removeFriendship(userId, friendId);
    }

    public List<User> showMutualFriends(Long userId1, Long userId2) {
        return userStorage.getMutualFriends(userId1, userId2);
>>>>>>> 97c2343 (скопирован мейн для группового занятия)
    }
}
