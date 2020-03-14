FROM openjdk:8-jdk-alpine as build
COPY . /src
WORKDIR /src
RUN ./gradlew clean shadowJar

FROM openjdk:8-jre-slim as base
COPY --from=build /src/build/*.jar /app/bam-aggregate.jar
