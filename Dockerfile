#
FROM  maven:3.8.3-openjdk-17 as build
COPY . .
RUN mvn clean package -DskipTests

#
# Package stage
#
FROM openjdk:17-jdk-slim
COPY --from=build /target telegram-java-bot-0.0.1-SNAPSHOT.jar
# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","telegram-java-bot-0.0.1-SNAPSHOT.jar"]