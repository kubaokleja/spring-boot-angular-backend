FROM eclipse-temurin:11-alpine
RUN mkdir /app
COPY target/springboot-angular-0.0.1.jar /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/springboot-angular-0.0.1.jar"]