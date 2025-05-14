from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import os
import json
from typing import List, Optional

app = FastAPI(title="TCM NLP Processor")

class SymptomInput(BaseModel):
    text: str

class AnalysisResult(BaseModel):
    bodyType: str
    mainSyndrome: str
    suggestions: List[str]

@app.get("/")
def read_root():
    return {"status": "TCM NLP Service is running"}

@app.post("/analyze", response_model=AnalysisResult)
def analyze_symptoms(input: SymptomInput):
    """
    分析症状文本并返回中医辨证结果
    """
    try:
        # 简单模拟分析过程 (实际会使用NLP模型)
        if "头痛" in input.text:
            return {
                "bodyType": "阳虚",
                "mainSyndrome": "肝阳上亢",
                "suggestions": [
                    "注意休息，避免过度劳累",
                    "建议食用菊花茶，有清肝明目的功效",
                    "可以按摩太阳穴，缓解头痛症状"
                ]
            }
        elif "失眠" in input.text:
            return {
                "bodyType": "阴虚",
                "mainSyndrome": "心脾两虚",
                "suggestions": [
                    "晚上避免饮用咖啡、茶等刺激性饮品",
                    "睡前可以泡脚，有助于安神",
                    "可以适量食用小米粥，有养心安神的作用",
                    "建议保持规律的作息时间"
                ]
            }
        elif "腹泻" in input.text:
            return {
                "bodyType": "脾虚",
                "mainSyndrome": "脾胃虚弱",
                "suggestions": [
                    "饮食宜清淡，少食生冷食物",
                    "可以食用白米粥，帮助恢复肠胃功能",
                    "注意保暖，避免着凉",
                    "适量运动，增强脾胃功能"
                ]
            }
        else:
            return {
                "bodyType": "气虚",
                "mainSyndrome": "气血不足",
                "suggestions": [
                    "保持充足的睡眠，避免过度疲劳",
                    "可以食用黄芪炖鸡汤，有补气血的功效",
                    "适量运动，如太极拳、散步等",
                    "保持心情舒畅，避免精神压力过大"
                ]
            }
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"分析失败: {str(e)}")

@app.get("/health")
def health_check():
    return {"status": "healthy"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=5000, reload=True) 