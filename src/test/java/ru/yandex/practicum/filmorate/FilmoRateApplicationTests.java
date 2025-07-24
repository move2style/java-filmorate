package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
public class FilmoRateApplicationTests {

    private final UserDbStorage userStorage;

    private Long userId;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2008, 12, 1));
        userStorage.postUser(user);

        Optional<User> createdUser = userStorage.getUsers().stream().findFirst();
        assertThat(createdUser).isPresent();
        userId = createdUser.get().getId();
    }

    @Test
    public void testFindUserById() {
        Optional<User> userOptional = Optional.ofNullable(userStorage.findUser(userId));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", userId)
                );
    }

    @Test
    public void testFindAll() {
        Collection<User> allUsers = userStorage.getUsers();
        assertThat(allUsers).isNotNull().isNotEmpty();

        assertThat(allUsers)
                .anySatisfy(user -> assertThat(user.getId()).isEqualTo(userId));
    }

    @Test
    public void testUpdateUserById() {
        User user = userStorage.findUser(userId);
        assertThat(user).isNotNull();

        user.setEmail("test@example.com");
        user.setLogin("testloginTEST");
        user.setName("Test UserTEST");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        userStorage.updateUser(user);

        User updatedUser = userStorage.findUser(userId);

        assertThat(updatedUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("login", "testloginTEST");
    }
}