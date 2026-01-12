package ru.otus.java.basic.chat.dto;

public class PresenceEvent {
    private Long userId;
    private boolean online;

    public Long getUserId() {
        return userId;
    }

    public boolean isOnline() {
        return online;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}