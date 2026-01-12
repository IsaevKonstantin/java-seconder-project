package ru.otus.java.basic.friendship.model;

public enum FriendshipStatus {
    ACCEPTED("accepted"), PENDING("pending"), BLOCKED("blocked");

    private final String dbValue;

    FriendshipStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static FriendshipStatus fromDbValue(String value) {
        if (value == null) {
            return null;
        }

        for (FriendshipStatus status : values()) {
            if (status.dbValue.equalsIgnoreCase(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown friendship status: " + value);
    }
}
