# PowerShell部署脚本

# 检查环境变量
if (-not $env:DEEPSEEK_KEY) {
    Write-Error "错误: 环境变量DEEPSEEK_KEY未设置"
    Write-Host "请先设置: $env:DEEPSEEK_KEY='您的API密钥'"
    exit 1
}

# Base64编码API密钥
$apiKeyBytes = [System.Text.Encoding]::UTF8.GetBytes($env:DEEPSEEK_KEY)
$DEEPSEEK_KEY_BASE64 = [System.Convert]::ToBase64String($apiKeyBytes)

Write-Host "生成API密钥完成"

# 读取secrets模板并替换
$secretsTemplatePath = "kubernetes/manifests/secrets.yaml"
$secretsContent = Get-Content $secretsTemplatePath -Raw
$secretsContent = $secretsContent.Replace('${DEEPSEEK_KEY_BASE64}', $DEEPSEEK_KEY_BASE64)

# 应用密钥配置
Write-Host "应用密钥配置..."
$secretsContent | kubectl apply -f -

# 部署持久卷
Write-Host "部署持久卷..."
kubectl apply -f kubernetes/manifests/volumes.yaml

# 部署服务
Write-Host "部署服务..."
kubectl apply -f kubernetes/manifests/services.yaml

# 部署应用
Write-Host "部署应用..."
kubectl apply -f kubernetes/manifests/deployment.yaml

Write-Host "部署完成，正在检查服务状态..."
kubectl get pods
kubectl get services

Write-Host "前端服务外部IP (可能需要几分钟才能分配):"
kubectl get service tcm-frontend-service -o jsonpath="{.status.loadBalancer.ingress[0].ip}" 