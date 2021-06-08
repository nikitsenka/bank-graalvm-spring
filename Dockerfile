FROM adoptopenjdk/openjdk14

RUN apt-get update && \
      apt-get -y install sudo
RUN useradd -m -s /bin/sh appuser && echo "appuser:appuser" | chpasswd && adduser appuser sudo

WORKDIR /home/appuser

COPY build/libs/bank-graalvm-spring-*.jar app.jar
COPY src/main/resources config
#ISSUE https://github.com/spring-projects/spring-boot/issues/26627
COPY src/main/resources config/config
RUN chown appuser /var/log

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]