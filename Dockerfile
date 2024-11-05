FROM openjdk:17
EXPOSE 9191

COPY build/libs/*.jar /app/message-gateway.jar
WORKDIR /app

CMD ["java", "-jar", "/app/message-gateway.jar"]
