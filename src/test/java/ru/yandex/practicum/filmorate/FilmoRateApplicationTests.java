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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
public class FilmoRateApplicationTests {

    private final UserDbStorage userStorage;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2008, 12, 1));
        userStorage.postUser(user);
    }

    @Test
    public void testFindUserById() {
        Optional<User> userOptional = Optional.ofNullable(userStorage.findUser(1L));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testFindAll() {
        Collection<User> allUsers = userStorage.getUsers();
        assertThat(allUsers).isNotNull();
        assertThat(allUsers)
                .extracting("id")
                .notifyAll();
    }

    @Test
    public void testUpdateUserById() {
        Optional<User> userOptional = Optional.ofNullable(userStorage.findUser(1L));
        userOptional.get().setEmail("test@example.com");
        userOptional.get().setLogin("testloginTEST");
        userOptional.get().setName("Test UserTEST");
        userOptional.get().setBirthday(LocalDate.of(1990, 1, 1));

        userStorage.updateUser(userStorage.findUser(1L));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "testloginTEST")
                );
    }
}