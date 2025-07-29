package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert userSimpleJdbcInsert;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate,
                         NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.userSimpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        String sqlQuery = "select user_id, email, login, name, birthday from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User create(User element) {
        if (element.getName() == null || element.getName().isBlank()) {
            element.setName(element.getLogin());
        }
        Map<String, Object> params = new HashMap<>();
        params.put("login", element.getLogin());
        params.put("name", element.getName());
        params.put("email", element.getEmail());
        params.put("birthday", element.getBirthday());

        Number generatedId = userSimpleJdbcInsert.executeAndReturnKey(params);
        element.setId(generatedId.longValue());

        element.setFriends(new HashMap<>());
        return element;
    }


    @Override
    public User update(User newElement) {
        getOne(newElement.getId());

        if (newElement.getName() == null || newElement.getName().isBlank()) {
            newElement.setName(newElement.getLogin());
        }

        String sqlQuery = "update users set " +
                "login = ?, name = ?, email = ?, birthday = ? " +
                "where user_id = ?";
        jdbcTemplate.update(sqlQuery,
                newElement.getLogin(),
                newElement.getName(),
                newElement.getEmail(),
                newElement.getBirthday(),
                newElement.getId());

        return newElement;
    }

    @Override
    public User getOne(Long id) {
        User user;
        try {
            user = jdbcTemplate.queryForObject(
                    "select user_id, email, login, name, birthday from users where user_id = ?",
                    this::mapRowToUser,
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден.");
        }

        if (user != null) {
            user.setFriends(getFriendStatuses(id));
        }

        return user;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getObject("birthday", LocalDate.class))
                .build();
    }


    @Override
    public List<User> getFriends(Long id) {
        getOne(id);

        String sql = """
                SELECT u.user_id, u.email, u.login, u.name, u.birthday
                FROM friendship f
                JOIN users u ON f.addressed_id = u.user_id
                WHERE f.requester_id = ?
                AND f.status_id = (SELECT friendship_status_id FROM friendship_status WHERE name = 'FRIENDS')
                """;

        return jdbcTemplate.query(sql, this::mapRowToUser, id);
    }


    private Map<Long, FriendshipStatus> getFriendStatuses(Long id) {
        String sql = """
                SELECT addressed_id AS friend_id, s.name AS status
                FROM friendship f
                JOIN friendship_status s ON f.status_id = s.friendship_status_id
                WHERE requester_id = ?
                """;

        return jdbcTemplate.query(sql, rs -> {
            Map<Long, FriendshipStatus> friendMap = new HashMap<>();
            while (rs.next()) {
                long friendId = rs.getLong("friend_id");
                FriendshipStatus status = FriendshipStatus.valueOf(rs.getString("status"));
                friendMap.put(friendId, status);
            }
            return friendMap;
        }, id);
    }


    @Override
    public void addFriendship(long userId, long friendId, FriendshipStatus status) {
        long statusId = getStatusId(status.name());
        String sql = "INSERT INTO friendship (requester_id, addressed_id, status_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, statusId);
    }

    @Override
    public List<User> getMutualFriends(Long userId1, Long userId2) {
        String sql = """
                SELECT u.user_id, u.email, u.login, u.name, u.birthday
                FROM users u
                WHERE u.user_id IN (
                  SELECT addressed_id
                  FROM friendship
                  WHERE requester_id IN (:userA, :userB)
                  GROUP BY addressed_id
                  HAVING COUNT(*) = 2);
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("userA", userId1);
        params.put("userB", userId2);

        return namedParameterJdbcTemplate.query(sql, params, this::mapRowToUser);
    }


    @Override
    public void updateFriendshipStatus(long userId, long friendId, FriendshipStatus status) {
        long statusId = getStatusId(status.name());
        String sql = "UPDATE friendship SET status_id = ? WHERE requester_id = ? AND addressed_id = ?";
        jdbcTemplate.update(sql, statusId, userId, friendId);
    }

    @Override
    public void removeFriendship(long userId, long friendId) {
        jdbcTemplate.update(
                "DELETE FROM friendship WHERE requester_id = ? AND addressed_id = ?",
                userId, friendId
        );
    }

    @Override
    public Optional<FriendshipStatus> getFriendshipStatus(long userId, long friendId) {
        String sql = "SELECT fs.name FROM friendship f JOIN friendship_status fs " +
                "ON f.status_id = fs.friendship_status_id WHERE requester_id = ? AND addressed_id = ?";
        try {
            String statusName = jdbcTemplate.queryForObject(sql, String.class, userId, friendId);
            return Optional.of(FriendshipStatus.valueOf(statusName));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private long getStatusId(String statusName) {
        String sql = "SELECT friendship_status_id FROM friendship_status WHERE name = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, statusName);
    }

    private Set<Long> getFriendsIds(Long id) {
        String friendsSql = "SELECT requester_id, addressed_id FROM friendship f " +
                "JOIN friendship_status fs ON f.status_id = fs.friendship_status_id " +
                "WHERE (f.requester_id = ? OR f.addressed_id = ?) AND fs.name = 'CONFIRMED'";

        Set<Long> friendIds = new HashSet<>();

        jdbcTemplate.query(friendsSql, rse -> {
            long requesterId = rse.getLong("requester_id");
            long addressedId = rse.getLong("addressed_id");
            if (requesterId == id) {
                friendIds.add(addressedId);
            } else {
                friendIds.add(requesterId);
            }
        }, id, id);

        return friendIds;
    }
}
