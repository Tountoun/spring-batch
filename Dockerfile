FROM eclipse-temurin:8

LABEL maintainer='tountounabela@gmail.com'

WORKDIR /app

COPY target/spring-batch-0.0.1-SNAPSHOT.jar /app/spring-batch-app.jar

ENTRYPOINT ["java", "-jar", "spring-batch-app.jar", "--spring.profiles.active=prod"]