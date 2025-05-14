FROM maven:3.8.4-openjdk-11-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/ /app/src/
RUN mvn package -DskipTests

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/tcm-robot-backend.jar /app/app.jar

# 设置时区为中国标准时间
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建存放知识库的目录
RUN mkdir -p /app/knowledge
VOLUME /app/knowledge

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"] 