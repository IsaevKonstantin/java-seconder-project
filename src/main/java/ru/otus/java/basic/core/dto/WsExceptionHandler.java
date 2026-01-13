package ru.otus.java.basic.core.dto;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;

@ControllerAdvice
public class WsExceptionHandler {
    private final SimpMessagingTemplate messagingTemplate;

    public WsExceptionHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageExceptionHandler
    public void handle(RuntimeException e, Principal principal) {
        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/errors",
                e.getMessage()
        );
    }
}

