FROM openjdk:8

EXPOSE 8081

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} authentication-service-1.0.jar

ENTRYPOINT ["java", "-jar", "authentication-service-1.0.jar"]

