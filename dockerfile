FROM openjdk:11-jre-slim
ADD ./target/onm-auth-0.0.1-SNAPSHOT.jar onm-auth-0.0.1-SNAPSHOT.jar
ENV JAVA_OPTS=""
ENTRYPOINT ["java", "-jar", "/onm-auth-0.0.1-SNAPSHOT.jar"]