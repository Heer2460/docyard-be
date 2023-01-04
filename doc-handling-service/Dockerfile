FROM openjdk:8

EXPOSE 8084

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} doc-handling-service-1.0.jar

ENTRYPOINT ["java", "-jar", "doc-handling-service-1.0.jar"]

