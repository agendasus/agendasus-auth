FROM openjdk:8u232-jdk-slim-buster
RUN cp /usr/share/zoneinfo/America/Sao_Paulo /etc/localtime
RUN apt-get update -y && apt-get install wget -y
RUN echo "America/Sao_Paulo" > /etc/timezone
VOLUME /tmp
EXPOSE 9100
COPY target/AgendaSUS-Auth-1.0.0.jar app.jar
ENTRYPOINT exec java -Djava.security.egd=file:/dev/./urandom $JAVA_OPTS -jar /app.jar