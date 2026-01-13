package ru.otus.java.basic.friendship.dto;

import ru.otus.java.basic.user.dto.UserResponse;
import ru.otus.java.basic.friendship.model.FriendshipStatus;

public class ContactResponse extends UserResponse {
    private FriendshipStatus status;
    private Long initiatorId;

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public Long getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(Long initiatorId) {
        this.initiatorId = initiatorId;
    }
}
