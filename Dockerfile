FROM maven:3.9.5-amazoncorretto-17 AS build
COPY ./pom.xml .
COPY ./src ./src
RUN mvn package

FROM amazoncorretto:17-alpine AS corretto-jdk
RUN apk add --no-cache binutils
RUN jlink \
         --add-modules ALL-MODULE-PATH \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /jre

FROM alpine:3.18.4
COPY --from=corretto-jdk /jre $JAVA_HOME
COPY --from=build /target/*.jar app.jar
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]