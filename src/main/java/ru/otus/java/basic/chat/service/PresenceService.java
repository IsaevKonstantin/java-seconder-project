package ru.otus.java.basic.chat.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PresenceService {
    private final Map<Long, Set<String>> session = new ConcurrentHashMap<>();

    public boolean online(Long userId, String sessionId) {
        session.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet())
                .add(sessionId);

        return session.get(userId).size() == 1;
    }

    public boolean offline(Long userId, String sessionId) {
        Set<String> userSession = session.get(userId);

        if (userSession == null) return false;

        userSession.remove(sessionId);

        if (userSession.isEmpty()) {
            session.remove(userId);
            return true;
        }

        return false;
    }

    public Set<Long> getOnlineUsers() {
        return session.keySet();
    }

    public boolean isOnline(Long userId) {
        return session.containsKey(userId);
    }
}
