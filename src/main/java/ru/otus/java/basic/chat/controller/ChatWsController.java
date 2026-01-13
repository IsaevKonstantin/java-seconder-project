package ru.otus.java.basic.chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.otus.java.basic.chat.dto.ChatMessage;
import ru.otus.java.basic.chat.service.PresenceService;
import ru.otus.java.basic.friendship.service.FriendshipService;

import java.security.Principal;

@Controller
public class ChatWsController {
    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;
    private final FriendshipService friendshipService;

    public ChatWsController(
            SimpMessagingTemplate messagingTemplate,
            FriendshipService friendshipService,
            PresenceService presenceService
    ) {
        this.messagingTemplate = messagingTemplate;
        this.friendshipService = friendshipService;
        this.presenceService = presenceService;
    }

    @MessageMapping("/chat.send")
    public void send(ChatMessage message, Principal principal) {
        Long fromUserId = Long.valueOf(principal.getName());
        Long toUserId = message.getToUserId();

        if (!friendshipService.areFriends(fromUserId, toUserId)) {
            throw new RuntimeException("Users are not friends");
        }

        message.setFromUserId(fromUserId);
        message.setTimestamp(System.currentTimeMillis());

        messagingTemplate.convertAndSendToUser(
                toUserId.toString(),
                "/queue/messages",
                message
        );
    }

    @MessageMapping("/presence/init")
    public void init(Principal principal) {
        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/presence/init",
                presenceService.getOnlineUsers()
        );
    }
}
