package ru.otus.java.basic.user.dao;

import ru.otus.java.basic.user.model.User;
import ru.otus.java.basic.core.db.Database;

import java.sql.*;
import java.util.Optional;

public class UserDao {

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

    public Optional<User> findById(Long id) {

        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

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

    public int update(User user) {

        String sql = """
                UPDATE users
                SET login = ?,
                    first_name = ?,
                    last_name = ?,
                    patronymic = ?,
                    phone = ?,
                    email = ?
                WHERE id = ?
                """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, user.getLogin());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getPatronymic());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getEmail());
            ps.setLong(7, user.getId());

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error: update user", e);
        }
    }

    public int updatePassword(User user) {

        String sql = """
                UPDATE users
                SET password_hash = ?
                WHERE id = ?
                """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, user.getPasswordHash());
            ps.setLong(2, user.getId());

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error: update password", e);
        }
    }
}
