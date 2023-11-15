FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY build/libs/recipe-forum-0.0.1.jar /app/
ENTRYPOINT ["java","-jar","/app/recipe-forum-0.0.1.jar"]