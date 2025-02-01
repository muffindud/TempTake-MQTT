FROM gradle:8.4-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew shadowJar --no-daemon

FROM openjdk:21-jdk-slim
RUN mkdir /app
COPY --from=build /home/gradle/src/app/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]