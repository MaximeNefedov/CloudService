FROM dvmarques/openjdk-14-jdk-alpine-with-timezone

ADD target/cloud_service_app-0.0.1-SNAPSHOT.jar cloud_service_app.jar

WORKDIR /src

COPY mime_types.txt /src

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "/cloud_service_app.jar"]