FROM openjdk:8

EXPOSE 8080

ADD target/dockyard-be.jar dockyard-be.jar
 
ENTRYPOINT ["java","-jar", "dockyard-be.jar"]
