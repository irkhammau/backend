FROM openjdk:11
VOLUME "/tmp"
ADD target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT [ "java","-jar", "/app.jar" ]