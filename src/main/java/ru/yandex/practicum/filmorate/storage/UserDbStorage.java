package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage{
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User postUser(User user) {
        UserService.validateUser(user);

        String userName = user.getName();
        if (userName == null) {
            userName = user.getLogin();
        } else if (userName.isEmpty()) {
            userName = user.getLogin();
        }

        Map<String, Object> values = new HashMap<>();
        values.put("name", userName);
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("birthday", user.getBirthday());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        return findUser(simpleJdbcInsert.executeAndReturnKey(values).longValue());
    }

    @Override
    public List<User> getUsers() {
        String sql = "select * from users";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update users set " +
                "name = ?, login = ?, email = ?, birthday = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());

        if (user.getFriends() != null) {
            String friendsSqlQuery = "delete from friends where user_id = ?";
            jdbcTemplate.update(friendsSqlQuery, user.getId());
            for (Long friendId : user.getFriends()) {
                friendsSqlQuery = "insert into friends(user_id, friend_id, status) " +
                        "values (?, ?, ?)";
                jdbcTemplate.update(friendsSqlQuery,
                        user.getId(),
                        friendId,
                        false);
            }
        }

        return findUser(user.getId());
    }

    @Override
    public User findUser(Long id) {

        String sql = "select * from users where id = ?";

        List<User> userCollection = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
        if (userCollection.size() == 1) {
            return userCollection.get(0);
        } else {
            throw new NotFoundException(String.format("Пользователя с id-%d не существует.", id));
        }
    }

    @Override
    public boolean delete(Long id) {
        String sqlQuery = "delete from mpa where id = ?";
        jdbcTemplate.update(sqlQuery, id);

        sqlQuery = "delete from mpa where id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String login = rs.getString("login");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        String friendsSql = "select * from friends where user_id = ?";
        List<Long> friendsCollection = jdbcTemplate.query(friendsSql, (rs1, rowNum) -> Long.valueOf(makeUserFriend(rs1)), id);

        return new User(id, name, email, login, birthday, new HashSet<>(friendsCollection));
    }

    private Integer makeUserFriend(ResultSet rs) throws SQLException {
        Integer friendId = rs.getInt("friend_id");
        return friendId;
    }

    @Override
    public long getNextId() {
        HashMap<Object, Object> users = null;
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> (long) id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
