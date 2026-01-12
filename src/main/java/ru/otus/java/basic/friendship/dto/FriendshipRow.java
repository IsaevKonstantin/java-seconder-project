package ru.otus.java.basic.friendship.dto;

public class FriendshipRow {
    private long userId;
    private long friendId;
    private String status;

    public long getUserId() {
        return userId;
    }

    public long getFriendId() {
        return friendId;
    }

    public String getStatus() {
        return status;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setFriendId(long friendId) {
        this.friendId = friendId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
