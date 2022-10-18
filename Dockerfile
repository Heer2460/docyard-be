FROM openjdk:8
COPY target/*.jar app.jar
ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT ["sh", "-c" "java $jAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
