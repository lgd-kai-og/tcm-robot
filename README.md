<<<<<<< HEAD
# 中医机器人系统 (TCM Robot System)

基于Docker容器技术的中医智能问诊系统，提供辨证分析与调理建议。

## 系统架构

系统采用三层架构设计：

- **交互层**：Vue.js构建的前端界面实现中医问诊对话窗口，通过WebSocket协议与后端通信
- **服务层**：Spring Boot微服务架构实现中医知识库管理、对话逻辑处理及腾讯元宝DeepSeek API对接
- **智能层**：集成自然语言，将用户症状描述转化为结构化问诊参数，调用中医辨证算法生成调理建议

## 目录结构

```
tcm-robot/
├── frontend/                       # Vue.js前端项目
│   ├── src/                        # 源代码
│   ├── Dockerfile                  # 前端容器构建文件
│   ├── nginx.conf                  # Nginx配置
│   └── package.json                # 依赖管理
│
├── backend/                        # Spring Boot后端服务
│   ├── src/                        # 源代码
│   ├── Dockerfile                  # 后端容器构建文件
│   └── pom.xml                     # Maven依赖管理
│
├── redis/                          # Redis缓存配置
│   ├── Dockerfile                  # Redis容器构建文件
│   └── redis.conf                  # Redis配置文件 (设置LRU策略)
│
├── knowledge-updater/              # 知识库更新服务
│   ├── Dockerfile                  # 容器构建文件
│   └── update-knowledge.sh         # 自动更新脚本
│
├── kubernetes/                     # Kubernetes部署文件
│   └── manifests/                  # K8s资源定义
│       ├── deployment.yaml         # 部署配置
│       ├── services.yaml           # 服务配置
│       ├── volumes.yaml            # 持久卷配置
│       └── secrets.yaml            # 密钥配置
│
├── docker-compose.yml              # 容器编排定义
└── traefik.yml                     # Traefik网关配置
```

## 部署方式

### 使用Docker Compose部署

1. 首先设置API密钥环境变量：

```bash
export DEEPSEEK_KEY=sk-e0eb4c2cc5f742269d13223dac47050d
```

2. 启动服务：

```bash
docker-compose up -d
```

### 使用Kubernetes部署

1. 创建密钥：

```bash
# 对API密钥进行Base64编码
DEEPSEEK_KEY_BASE64=$(echo -n "sk-e0eb4c2cc5f742269d13223dac47050d" | base64)

# 设置环境变量
set DEEPSEEK_KEY_BASE64=$DEEPSEEK_KEY_BASE64

# 应用密钥配置
envsubst < kubernetes/manifests/secrets.yaml | kubectl apply -f -
```
powershell方案：
# 1. 定义你的 API 密钥明文
$apiKeyPlainText = "sk-e0eb4c2cc5f742269d13223dac47050d"

# 2. 将 API 密钥转换为 UTF-8 字节数组
$apiKeyBytes = [System.Text.Encoding]::UTF8.GetBytes($apiKeyPlainText)

# 3. 将字节数组进行 Base64 编码
$DEEPSEEK_KEY_BASE64_VALUE = [System.Convert]::ToBase64String($apiKeyBytes)

# 4. 打印出来确认一下（可选）
Write-Host "Base64 Encoded Key: $DEEPSEEK_KEY_BASE64_VALUE"

# 5. 读取 secrets.yaml 模板文件内容
#    确保路径正确，如果您的 PowerShell 当前目录不是项目根目录，请使用绝对路径
$secretTemplatePath = "kubernetes/manifests/secrets.yaml"
if (-not (Test-Path $secretTemplatePath)) {
    Write-Error "错误: 找不到 secrets.yaml 文件路径 '$secretTemplatePath'"
    exit 1
}
$secretTemplateContent = Get-Content $secretTemplatePath -Raw

# 6. 替换占位符
#    PowerShell 的 .Replace() 方法是区分大小写的。
#    这里的 '${DEEPSEEK_KEY_BASE64}' 字符串需要与您 secrets.yaml 文件中的占位符完全匹配。
$placeholder = '${DEEPSEEK_KEY_BASE64}' # 确保这与您文件中的占位符完全一样
$processedSecretContent = $secretTemplateContent.Replace($placeholder, $DEEPSEEK_KEY_BASE64_VALUE)

# 7. 打印处理后的内容看看是否正确（可选）
Write-Host "--- Processed Secret Content ---"
Write-Host $processedSecretContent
Write-Host "-----------------------------"

# 8. 将处理后的内容通过管道传递给 kubectl
#    确保 kubectl 已经配置好并能连接到您的 Kubernetes 集群
$processedSecretContent | kubectl apply -f -
2. 部署持久卷：

```bash
kubectl apply -f kubernetes/manifests/volumes.yaml
```

3. 部署服务和应用：

```bash
kubectl apply -f kubernetes/manifests/services.yaml
kubectl apply -f kubernetes/manifests/deployment.yaml
```
kubectl get service tcm-frontend-service
http://10.106.201.125


cd E:\houduan\tcm-robot
.\kubernetes\deploy-windows.ps1
## 特色功能

- **知识库动态加载**：中医经典方剂库以ReadOnly卷形式挂载，通过CRON定时更新
- **智能问诊流程**：集成腾讯元宝DeepSeek API进行中医辨证分析
- **中医辨证缓存优化**：基于Redis的LRU缓存淘汰策略，保留常见体质分析结果
- **问诊流量弹性伸缩**：通过HorizontalPodAutoscaler根据问诊量自动扩展服务实例
- **安全增强措施**：问诊数据加密，敏感操作审计

## 监控与运维

系统通过Prometheus和Grafana构建中医问诊质量监控看板，实时追踪六经辨证准确率和系统性能指标。 

docker --version
docker-compose --version
$env:DEEPSEEK_KEY="sk-482001d867ef4af19e83449c99c97377"
docker-compose build

docker-compose up -d

docker ps

docker-compose ps
=======
# tcm-robot
tcm-robot
>>>>>>> 5778939b503bfcada0c9fd2cf535302f5799a271
