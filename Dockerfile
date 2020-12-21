FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
ADD target/demo2-0.0.1-SNAPSHOT.jar demo2.jar
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -jar demo2.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
#ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar demo2.jar
