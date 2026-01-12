package ru.otus.java.basic.chat.listener;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import ru.otus.java.basic.chat.dto.PresenceEvent;
import ru.otus.java.basic.chat.service.PresenceService;

import java.util.Set;

@Component
public class PresenceListener {
    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;

    public PresenceListener(
            PresenceService presenceService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.presenceService = presenceService;
        this.messagingTemplate = messagingTemplate;
    }

    private Long extractUserId(AbstractSubProtocolEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        if (accessor.getUser() == null) {
            throw new RuntimeException("User not found in WebSocket session");
        }

        return Long.valueOf(accessor.getUser().getName());
    }

    private String getSessionId(AbstractSubProtocolEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        return accessor.getSessionId();
    }

    @EventListener
    public void onConnect(SessionConnectEvent event) {
        Long userId = extractUserId(event);
        String sessionId = getSessionId(event);

        if (userId == null) return;

        boolean firstSession = presenceService.online(userId, sessionId);

        if (!firstSession) return;

        PresenceEvent presenceEvent = new PresenceEvent();

        presenceEvent.setUserId(userId);
        presenceEvent.setOnline(true);

        messagingTemplate.convertAndSend(
                "/topic/presence",
                presenceEvent
        );
    }

    @EventListener
    public void onSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        if (accessor.getUser() == null) return;

        String destination = accessor.getDestination();
        if (!"/user/queue/presence/init".equals(destination)) return;

        Long userId = Long.valueOf(accessor.getUser().getName());

        Set<Long> onlineUsers = presenceService.getOnlineUsers();

        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/presence/init",
                onlineUsers
        );
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        Long userId = extractUserId(event);
        String sessionId = getSessionId(event);

        if (userId == null) return;

        boolean lastSession = presenceService.offline(userId, sessionId);

        if (!lastSession) return;

        PresenceEvent presenceEvent = new PresenceEvent();

        presenceEvent.setUserId(userId);
        presenceEvent.setOnline(false);

        messagingTemplate.convertAndSend(
                "/topic/presence",
                presenceEvent
        );
    }
}
