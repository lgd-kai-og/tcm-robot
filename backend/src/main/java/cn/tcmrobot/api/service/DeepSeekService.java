package cn.tcmrobot.api.service;

import cn.tcmrobot.api.model.DiagnosisResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class DeepSeekService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${deepseek.api.key}")
    private String apiKey;
    
    @Value("${deepseek.api.url:https://api.deepseek.cn/v1/chat/completions}")
    private String apiUrl;
    
    @Value("${deepseek.api.model:tcm-pro-1.0}")
    private String model;

    @Cacheable(value = "diagnosisResults", key = "#symptomText", unless = "#result == null")
    public DiagnosisResult analyzeSymptom(String symptomText) {
        try {
            String prompt = "作为中医机器人，请对以下症状进行辨证分析。请使用JSON格式返回，包含体质类型(bodyType)、主要证型(mainSyndrome)和3-5条调理建议(suggestions)。症状描述：" + symptomText;
            
            String jsonPayload = objectMapper.writeValueAsString(new Object() {
                public final String model = DeepSeekService.this.model;
                public final List<Object> messages = List.of(
                    new Object() {
                        public final String role = "user";
                        public final String content = prompt;
                    }
                );
                public final double temperature = 0.7;
            });
            
            String response = callDeepSeekApi(jsonPayload);
            return parseResponse(response);
        } catch (Exception e) {
            log.error("Failed to analyze symptoms", e);
            return DiagnosisResult.builder()
                    .bodyType("未知")
                    .mainSyndrome("分析失败")
                    .suggestions(Arrays.asList("系统暂时无法分析，请稍后再试。"))
                    .build();
        }
    }
    
    private String callDeepSeekApi(String jsonPayload) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + apiKey);
            httpPost.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));
            
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity, StandardCharsets.UTF_8);
                } else {
                    throw new IOException("Empty response from DeepSeek API");
                }
            }
        }
    }
    
    private DiagnosisResult parseResponse(String response) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(response);
        String content = root.path("choices").path(0).path("message").path("content").asText();
        
        try {
            // 尝试直接解析JSON
            return objectMapper.readValue(content, DiagnosisResult.class);
        } catch (Exception e) {
            log.warn("Failed to parse direct JSON response, trying to extract JSON from text", e);
            
            // 尝试从文本中提取JSON部分
            int startIndex = content.indexOf("{");
            int endIndex = content.lastIndexOf("}") + 1;
            
            if (startIndex >= 0 && endIndex > startIndex) {
                String jsonPart = content.substring(startIndex, endIndex);
                try {
                    return objectMapper.readValue(jsonPart, DiagnosisResult.class);
                } catch (Exception ex) {
                    log.error("Failed to parse JSON part", ex);
                }
            }
            
            // 如果解析失败，创建一个基本响应
            return DiagnosisResult.builder()
                    .bodyType("未能识别")
                    .mainSyndrome("解析失败")
                    .suggestions(Arrays.asList("AI返回的结果无法解析，请重试。", "原始回复: " + (content.length() > 100 ? content.substring(0, 100) + "..." : content)))
                    .build();
        }
    }
} 