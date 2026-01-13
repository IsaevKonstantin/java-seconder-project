package ru.otus.java.basic.user.dao;

import ru.otus.java.basic.core.db.Database;
import ru.otus.java.basic.user.model.UserProfile;

import java.sql.*;
import java.util.Optional;

public class UserProfileDao {

    public Optional<UserProfile> findByUserId(Long userId) {

        String sql = "select * from user_profiles where user_id = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                UserProfile p = new UserProfile();
                p.setUserId(rs.getLong("user_id"));
                p.setAbout(rs.getString("about"));
                p.setAvatar(rs.getBytes("avatar"));
                return Optional.of(p);
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int update(UserProfile profile) {

        String sql = """
                UPDATE user_profiles
                SET about = ?,
                    avatar = ?
                WHERE user_id = ?
                """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, profile.getAbout());
            ps.setBytes(2, profile.getAvatar());
            ps.setLong(3, profile.getUserId());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("DB error: update profile", e);
        }
    }
}
