package cn.tcmrobot.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisResult {
    private String bodyType;        // 体质类型，如"阴虚"、"阳虚"、"气虚"等
    private String mainSyndrome;    // 主要证型
    private List<String> suggestions; // 调理建议列表
} 