FROM openjdk:8
EXPOSE 8761
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} registry-service-1.0.jar
ENTRYPOINT ["java", "-jar", "registry-service-1.0.jar"]

