package ru.otus.java.basic.auth.dao;

import ru.otus.java.basic.core.db.Database;
import ru.otus.java.basic.user.model.User;

import java.sql.*;
import java.util.Optional;

public class AuthDao {

    private User map(ResultSet rs) throws SQLException {
        User u = new User();

        u.setId(rs.getLong("id"));
        u.setLogin(rs.getString("login"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFirstName(rs.getString("first_name"));
        u.setLastName(rs.getString("last_name"));
        u.setPatronymic(rs.getString("patronymic"));
        u.setPhone(rs.getString("phone"));
        u.setEmail(rs.getString("email"));

        return u;
    }

    public Optional<User> findByLogin(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) return Optional.of(map(rs));

            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("DB error", e);
        }
    }

    public boolean existsByLogin(String login) {
        return findByLogin(login).isPresent();
    }

    public void save(User user) {
        String sql = """
                INSERT INTO users
                (login, password_hash, first_name, last_name, patronymic, phone, email)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFirstName());
            ps.setString(4, user.getLastName());
            ps.setString(5, user.getPatronymic());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getEmail());

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();

            if (keys.next()) user.setId(keys.getLong(1));
        } catch (SQLException e) {
            throw new RuntimeException("DB error", e);
        }
    }

    public void saveProfileIfNotExists(Long userId) {
        String sql = "insert into user_profiles(user_id) values (?) on conflict do nothing";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, userId);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}