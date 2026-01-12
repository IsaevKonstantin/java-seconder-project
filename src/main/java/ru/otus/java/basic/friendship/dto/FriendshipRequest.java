package ru.otus.java.basic.friendship.dto;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import ru.otus.java.basic.friendship.model.FriendshipStatus;

public class FriendshipRequest {
    @NotBlank
    private Long friendId;
    @NotBlank
    private FriendshipStatus status;

    public Long getFriendId() {
        return friendId;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }
}
