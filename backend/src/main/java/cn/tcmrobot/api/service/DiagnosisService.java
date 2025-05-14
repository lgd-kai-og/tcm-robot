package cn.tcmrobot.api.service;

import cn.tcmrobot.api.model.ChatMessage;
import cn.tcmrobot.api.model.DiagnosisResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final DeepSeekService deepSeekService;
    private final SimpMessagingTemplate messagingTemplate;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public void processUserMessage(String sessionId, String message) {
        log.info("Processing user message for session: {}", sessionId);
        
        // 立即发送确认消息
        sendChatMessage(sessionId, "正在为您分析，请稍候...");
        
        try {
            // 调用AI服务进行症状分析
            DiagnosisResult diagnosisResult = deepSeekService.analyzeSymptom(message);
            
            // 发送辨证结果
            sendDiagnosisResult(sessionId, diagnosisResult);
            
            // 发送辨证总结
            StringBuilder summary = new StringBuilder();
            summary.append("根据您的症状描述，您的体质类型可能是【")
                   .append(diagnosisResult.getBodyType())
                   .append("】，主要证型为【")
                   .append(diagnosisResult.getMainSyndrome())
                   .append("】。\n\n建议：\n");
            
            for (int i = 0; i < diagnosisResult.getSuggestions().size(); i++) {
                summary.append(i + 1).append(". ").append(diagnosisResult.getSuggestions().get(i)).append("\n");
            }
            
            sendChatMessage(sessionId, summary.toString());
            
        } catch (Exception e) {
            log.error("Error processing user message", e);
            sendChatMessage(sessionId, "很抱歉，处理您的请求时发生错误，请稍后再试。");
        }
    }
    
    private void sendChatMessage(String sessionId, String content) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.MessageType.CHAT);
        chatMessage.setContent(content);
        chatMessage.setSender("robot");
        chatMessage.setTime(LocalDateTime.now().format(timeFormatter));
        
        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, chatMessage);
    }
    
    private void sendDiagnosisResult(String sessionId, DiagnosisResult diagnosisResult) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.MessageType.DIAGNOSIS);
        chatMessage.setContent(diagnosisResult.toString());
        chatMessage.setSender("system");
        chatMessage.setTime(LocalDateTime.now().format(timeFormatter));
        
        messagingTemplate.convertAndSend("/topic/diagnosis/" + sessionId, diagnosisResult);
    }
} 