FROM openjdk:8

EXPOSE 8085

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} cron-job-service-1.0.jar

ENTRYPOINT ["java", "-jar", "cron-job-service-1.0.jar"]

