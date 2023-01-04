FROM openjdk:8

EXPOSE 8083

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} um-service-1.0.jar


ENTRYPOINT ["java", "-jar", "um-service-1.0.jar"]

