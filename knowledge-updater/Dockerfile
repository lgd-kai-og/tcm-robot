FROM alpine:3.14

# 安装必要的工具
RUN apk add --no-cache curl sqlite bash tzdata jq

# 设置中国时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建工作目录
WORKDIR /app

# 复制更新脚本
COPY update-knowledge.sh /app/
RUN chmod +x /app/update-knowledge.sh

# 设置知识库卷
VOLUME /tcm_knowledge

# 设置cron任务 - 每天凌晨3点执行更新
RUN echo "0 3 * * * /app/update-knowledge.sh > /proc/1/fd/1 2>/proc/1/fd/2" > /etc/crontabs/root

# 启动cron服务
CMD crond -f -l 8 