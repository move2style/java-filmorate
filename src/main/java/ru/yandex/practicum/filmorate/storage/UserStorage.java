package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage extends BaseStorage<User> {
    List<User> getFriends(Long id);

    Optional<FriendshipStatus> getFriendshipStatus(long userId, long friendId);

    void removeFriendship(long userId, long friendId);

    void updateFriendshipStatus(long userId, long friendId, FriendshipStatus status);

    long getNextId();

    boolean delete(Long id);
}
