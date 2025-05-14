FROM python:3.9-slim

WORKDIR /app

RUN pip install --no-cache-dir torch==1.9.1 transformers==4.11.3 fastapi==0.70.0 uvicorn==0.15.0 pydantic==1.8.2

COPY . .

# 创建模型目录
RUN mkdir -p /app/models
VOLUME /app/models

EXPOSE 5000

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "5000"] 