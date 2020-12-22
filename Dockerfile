FROM adoptopenjdk/openjdk11
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY target/headlines-0.0.1-SNAPSHOT.jar news.jar
EXPOSE 8001
ENTRYPOINT exec java $JAVA_OPTS -jar news.jar --weatherapp.url=http://weather:8080/weather/v1/today/
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
#ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar news.jar
