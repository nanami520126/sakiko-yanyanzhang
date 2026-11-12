# ===== 构建阶段：Maven 打包 =====
FROM maven:3.8-openjdk-8 AS build
WORKDIR /app
COPY pom.xml .
COPY sakiko-common ./sakiko-common
COPY sakiko-pojo ./sakiko-pojo
COPY sakiko-server ./sakiko-server
RUN mvn clean package -DskipTests

# ===== 运行阶段：JRE 运行 =====
FROM openjdk:8-jre-slim
WORKDIR /app
COPY --from=build /app/sakiko-server/target/sakiko-server-1.0-SNAPSHOT.jar app.jar
ENV TZ=Asia/Shanghai
EXPOSE 8080
ENTRYPOINT ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "app.jar"]
