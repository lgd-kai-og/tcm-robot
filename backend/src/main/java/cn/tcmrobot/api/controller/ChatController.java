package cn.tcmrobot.api.controller;

import cn.tcmrobot.api.model.ChatMessage;
import cn.tcmrobot.api.service.DiagnosisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final DiagnosisService diagnosisService;
    private final Map<String, String> sessionIdMap = new ConcurrentHashMap<>();

    @MessageMapping("/chat.sendMessage")
    public void handleChatMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = getOrCreateSessionId(headerAccessor);
        log.info("Received message from session {}: {}", sessionId, chatMessage.getContent());
        
        // Process the user message
        diagnosisService.processUserMessage(sessionId, chatMessage.getContent());
    }

    @MessageMapping("/chat.connect")
    public void handleConnect(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = getOrCreateSessionId(headerAccessor);
        log.info("New user connected with session ID: {}", sessionId);
    }
    
    private String getOrCreateSessionId(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        if (!sessionIdMap.containsKey(sessionId)) {
            sessionIdMap.put(sessionId, UUID.randomUUID().toString());
        }
        return sessionIdMap.get(sessionId);
    }
} 