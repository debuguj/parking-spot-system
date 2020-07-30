ARG JAVA_VERSION=8
FROM openjdk:$JAVA_VERSION
ADD target/parking-spot-system-0.0.1-SNAPSHOT.jar parking-spot-system.jar
EXPOSE 8085
LABEL maintainer="GB"
ENTRYPOINT ["java","-Dspring.profiles.active=prod", "-jar", "parking-spot-system.jar"]
