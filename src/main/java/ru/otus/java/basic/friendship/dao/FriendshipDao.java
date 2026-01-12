package ru.otus.java.basic.friendship.dao;

import org.springframework.stereotype.Repository;
import ru.otus.java.basic.core.db.Database;
import ru.otus.java.basic.core.exceptions.InvalidFriendshipException;
import ru.otus.java.basic.friendship.dto.ContactResponse;
import ru.otus.java.basic.friendship.dto.FriendshipRow;
import ru.otus.java.basic.friendship.model.FriendshipStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Repository
public class FriendshipDao {

    private ContactResponse mapContact(ResultSet rs) throws SQLException {
        ContactResponse c = new ContactResponse();

        c.setId(rs.getLong("id"));
        c.setLogin(rs.getString("login"));
        c.setFirstName(rs.getString("first_name"));
        c.setLastName(rs.getString("last_name"));
        c.setPatronymic(rs.getString("patronymic"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setAbout(rs.getString("about"));
        byte[] avatar = rs.getBytes("avatar");
        if (avatar != null) {
            c.setAvatarBase64(
                    Base64.getEncoder().encodeToString(avatar)
            );
        }
        String status = rs.getString("friendship_status");
        c.setStatus(FriendshipStatus.fromDbValue(status));
        Object initiatorObj = rs.getObject("initiator_id");
        c.setInitiatorId(initiatorObj != null ? ((Number) initiatorObj).longValue() : null);

        return c;
    }

    private FriendshipRow mapFriendshipRow(ResultSet rs) throws SQLException {
        FriendshipRow f = new FriendshipRow();

        f.setUserId(rs.getLong("user_id"));
        f.setFriendId(rs.getLong("friend_id"));
        f.setStatus(rs.getString("status"));

        return f;
    }

    private void assertUserExists(Connection c, Long userId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT 1 FROM users WHERE id = ?")) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new InvalidFriendshipException("User not found");
                }
            }
        }
    }

    private Optional<FriendshipRow> findFriendship(
            Connection c, Long userId, Long friendId) throws SQLException {

        String sql = """
                SELECT user_id, friend_id, status
                FROM friendships
                WHERE least(user_id, friend_id) = least(?, ?)
                  AND greatest(user_id, friend_id) = greatest(?, ?)
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, friendId);
            ps.setLong(3, userId);
            ps.setLong(4, friendId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapFriendshipRow(rs));
                }
                return Optional.empty();
            }
        }
    }

    private void deleteFriendship(Connection c, Long userId, Long friendId) throws SQLException {

        try (PreparedStatement ps = c.prepareStatement("""
                DELETE FROM friendships
                WHERE least(user_id, friend_id) = least(?, ?)
                  AND greatest(user_id, friend_id) = greatest(?, ?)
                """)) {

            ps.setLong(1, userId);
            ps.setLong(2, friendId);
            ps.setLong(3, userId);
            ps.setLong(4, friendId);

            ps.executeUpdate();
        }
    }

    private void insertFriendship(
            Connection c, Long userId, Long friendId, String status) throws SQLException {

        try (PreparedStatement ps = c.prepareStatement("""
                INSERT INTO friendships (user_id, friend_id, status)
                VALUES (?, ?, ?)
                """)) {

            ps.setLong(1, userId);
            ps.setLong(2, friendId);
            ps.setString(3, status);

            ps.executeUpdate();
        }
    }

    private void updateFriendship(
            Connection c, Long userId, Long friendId, FriendshipStatus status) throws SQLException {

        try (PreparedStatement ps = c.prepareStatement("""
                UPDATE friendships
                SET status = ?
                WHERE least(user_id, friend_id) = least(?, ?)
                  AND greatest(user_id, friend_id) = greatest(?, ?)
                """)) {

            ps.setString(1, status.getDbValue());
            ps.setLong(2, userId);
            ps.setLong(3, friendId);
            ps.setLong(4, userId);
            ps.setLong(5, friendId);

            ps.executeUpdate();
        }
    }

    private Optional<ContactResponse> loadContactResponse(
            Connection c, Long userId, Long friendId) throws SQLException {

        String sql = """
                SELECT
                    u.login,
                    u.first_name,
                    u.last_name,
                    u.patronymic,
                    u.phone,
                    u.email,
                    u.id,
                    p.about,
                    p.avatar,
                    f.status AS friendship_status,
                    f.user_id AS initiator_id
                FROM friendships f
                JOIN users u
                    ON u.id = CASE
                        WHEN f.user_id = ? THEN f.friend_id
                        ELSE f.user_id
                    END
                LEFT JOIN user_profiles p
                    ON p.user_id = u.id
                WHERE least(f.user_id, f.friend_id) = least(?, ?)
                  AND greatest(f.user_id, f.friend_id) = greatest(?, ?)
                """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, userId);
            ps.setLong(3, friendId);
            ps.setLong(4, userId);
            ps.setLong(5, friendId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapContact(rs));
                }
            }
        }

        return Optional.empty();
    }

    private Optional<ContactResponse> loadContactResponseWithoutFriendship(
            Connection c, Long friendId) throws SQLException {

        String sql = """
            SELECT
                u.login,
                u.first_name,
                u.last_name,
                u.patronymic,
                u.phone,
                u.email,
                u.id,
                p.about,
                p.avatar,
                NULL AS friendship_status,
                NULL AS initiator_id
            FROM users u
            LEFT JOIN user_profiles p ON p.user_id = u.id
            WHERE u.id = ?
            """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, friendId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapContact(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<ContactResponse> getMyContacts(Long userId) {

        String sql = """
                SELECT
                    u.login,
                    u.first_name,
                    u.last_name,
                    u.patronymic,
                    u.phone,
                    u.email,
                    u.id,
                    p.about,
                    p.avatar,
                    f.status AS friendship_status,
                    f.user_id AS initiator_id
                FROM friendships f
                JOIN users u
                    ON u.id = CASE WHEN f.user_id = ? THEN f.friend_id ELSE f.user_id END
                LEFT JOIN user_profiles p
                    ON p.user_id = u.id
                WHERE ? IN (f.user_id, f.friend_id)
                """;

        List<ContactResponse> result = new ArrayList<>();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.add(mapContact(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("DB error", e);
        }

        return result;
    }

    public List<ContactResponse> searchNewContacts(Long myId, String query) {

        String sql = """
                SELECT
                    u.login,
                    u.first_name,
                    u.last_name,
                    u.patronymic,
                    u.phone,
                    u.email,
                    u.id,
                    p.about,
                    p.avatar,
                    NULL AS friendship_status,
                    NULL AS initiator_id
                FROM users u
                LEFT JOIN user_profiles p ON p.user_id = u.id
                WHERE u.id <> ?
                    AND (
                        LOWER(u.login) LIKE ?
                        OR LOWER(u.first_name) LIKE ?
                        OR u.phone LIKE ?
                    )
                    AND NOT EXISTS (
                        SELECT 1
                        FROM friendships f
                        WHERE (
                            (f.user_id = ? AND f.friend_id = u.id)
                            OR
                            (f.friend_id = ? AND f.user_id = u.id)
                        )
                    )
                LIMIT 20
                """;

        List<ContactResponse> users = new ArrayList<>();
        String likeQuery = "%" + query.toLowerCase() + "%";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, myId);
            ps.setString(2, likeQuery);
            ps.setString(3, likeQuery);
            ps.setString(4, likeQuery);
            ps.setLong(5, myId);
            ps.setLong(6, myId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                users.add(mapContact(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error", e);
        }

        return users;
    }

    public Optional<ContactResponse> addContact(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new InvalidFriendshipException("Unable to add yourself as a friend");
        }

        try (Connection c = Database.getConnection()) {
            assertUserExists(c, friendId);

            Optional<FriendshipRow> existing = findFriendship(c, userId, friendId);

            if (existing.isPresent()) {
                FriendshipRow row = existing.get();

                if (row.getStatus().equals("blocked")) {
                    if (row.getUserId() == userId) {
                        throw new InvalidFriendshipException("Unable to submit request - user blocked");
                    } else {
                        throw new InvalidFriendshipException("Failed to send request - user has blocked you");
                    }
                }

                throw new InvalidFriendshipException("The friend request already exists");
            }

            insertFriendship(c, userId, friendId, "pending");

            return loadContactResponse(c, userId, friendId);
        } catch (SQLException e) {
            throw new RuntimeException("DB error", e);
        }
    }

    public Optional<ContactResponse> approveContact(Long userId, Long contactId) {
        if (userId.equals(contactId)) {
            throw new InvalidFriendshipException("Unable to approve yourself");
        }

        try (Connection c = Database.getConnection()) {

            assertUserExists(c, contactId);

            FriendshipRow row = findFriendship(c, userId, contactId)
                    .orElseThrow(() -> new InvalidFriendshipException("Friend request does not exist"));

            FriendshipStatus status = FriendshipStatus.fromDbValue(row.getStatus());

            switch (status) {
                case BLOCKED -> {
                    if (row.getUserId() == userId) {
                        throw new InvalidFriendshipException("User is blocked");
                    } else {
                        throw new InvalidFriendshipException("User has blocked you");
                    }
                }
                case PENDING -> {
                    if (row.getUserId() == userId) {
                        throw new InvalidFriendshipException("Cannot approve your own request");
                    }
                }
                case ACCEPTED ->
                    throw new InvalidFriendshipException("User is already your friend");
            }

            updateFriendship(c, userId, contactId, FriendshipStatus.ACCEPTED);

            return loadContactResponse(c, userId, contactId);
        } catch (SQLException e) {
            throw new RuntimeException("DB error", e);
        }
    }

    public Optional<ContactResponse> cancelFriendshipReq(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new InvalidFriendshipException("Unable to cancel request to yourself");
        }

        try (Connection c = Database.getConnection()) {
            assertUserExists(c, friendId);

            FriendshipRow row = findFriendship(c, userId, friendId)
                    .orElseThrow(() ->
                            new InvalidFriendshipException(
                                    "Friend request does not exist"
                            )
                    );

            FriendshipStatus status =
                    FriendshipStatus.fromDbValue(row.getStatus());

            if (status != FriendshipStatus.PENDING) {
                throw new InvalidFriendshipException(
                        "Friendship is not pending"
                );
            }

            if (row.getUserId() != userId) {
                throw new InvalidFriendshipException(
                        "Cannot cancel someone else's request"
                );
            }

            deleteFriendship(c, userId, friendId);

            return loadContactResponseWithoutFriendship(c, friendId);

        } catch (SQLException e) {
            throw new RuntimeException("DB error", e);
        }
    }

    public Optional<ContactResponse> deleteFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new InvalidFriendshipException("Unable to delete yourself from friends");
        }

        try (Connection c = Database.getConnection()) {
            assertUserExists(c, friendId);

            FriendshipRow row = findFriendship(c, userId, friendId)
                    .orElseThrow(() ->
                            new InvalidFriendshipException("Friendship does not exist")
                    );

            FriendshipStatus status =
                    FriendshipStatus.fromDbValue(row.getStatus());

            if (status != FriendshipStatus.ACCEPTED) {
                throw new InvalidFriendshipException(
                        "Friendship is not accepted"
                );
            }

            if (row.getUserId() == userId) {
                deleteFriendship(c, userId, friendId);
                insertFriendship(
                        c,
                        friendId,
                        userId,
                        FriendshipStatus.PENDING.getDbValue()
                );
            } else {
                updateFriendship(
                        c,
                        userId,
                        friendId,
                        FriendshipStatus.PENDING
                );
            }

            return loadContactResponse(c, userId, friendId);

        } catch (SQLException e) {
            throw new RuntimeException("DB error", e);
        }
    }

    public Optional<ContactResponse> blockContact(Long userId, Long contactId) {
        if (userId.equals(contactId)) {
            throw new InvalidFriendshipException("Unable to block yourself");
        }

        try (Connection c = Database.getConnection()) {
            assertUserExists(c, contactId);

            Optional<FriendshipRow> existing = findFriendship(c, userId, contactId);

            if (existing.isPresent()) {
                FriendshipRow row = existing.get();

                if (row.getStatus().equals("blocked")) {
                    if (row.getUserId() == userId) {
                        throw new InvalidFriendshipException("User already blocked");
                    } else {
                        throw new InvalidFriendshipException("User has blocked you");
                    }
                }

                deleteFriendship(c, userId, contactId);
            }

            insertFriendship(c, userId, contactId, "blocked");

            return loadContactResponse(c, userId, contactId);
        } catch (SQLException e) {
            throw new RuntimeException("DB error", e);
        }
    }

    public boolean areFriends(Long userId, Long friendId) {

        try (Connection c = Database.getConnection()) {

            assertUserExists(c, friendId);

            Optional<FriendshipRow> existing = findFriendship(c, userId, friendId);

            return existing.isPresent() && existing.get().getStatus().equals(FriendshipStatus.ACCEPTED.getDbValue());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
