#!/bin/bash

# 设置API密钥
echo "生成API密钥..."
if [[ -z "${DEEPSEEK_KEY}" ]]; then
  echo "错误: 环境变量DEEPSEEK_KEY未设置"
  echo "请先设置: export DEEPSEEK_KEY=您的API密钥"
  exit 1
fi

# Base64编码API密钥
DEEPSEEK_KEY_BASE64=$(echo -n "${DEEPSEEK_KEY}" | base64)
export DEEPSEEK_KEY_BASE64

# 应用密钥配置
echo "应用密钥配置..."
envsubst < manifests/secrets.yaml | kubectl apply -f -

# 部署持久卷
echo "部署持久卷..."
kubectl apply -f manifests/volumes.yaml

# 部署服务
echo "部署服务..."
kubectl apply -f manifests/services.yaml

# 部署应用
echo "部署应用..."
kubectl apply -f manifests/deployment.yaml

echo "部署完成，正在检查服务状态..."
kubectl get pods
kubectl get services

echo "前端服务外部IP (可能需要几分钟才能分配):"
kubectl get service tcm-frontend-service -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 