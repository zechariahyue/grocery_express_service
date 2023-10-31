# use maven as a builder image to separate the compile step from our deployment image
FROM maven:3.8.1-openjdk-17-slim AS builder
WORKDIR app
COPY ./ ./
RUN mvn clean install

FROM openjdk:17-slim AS app
WORKDIR app
# copy the jar from the builder image to the final image
COPY --from=builder /app/target/assignment5-0.0.1-SNAPSHOT.jar /app/assignment5-0.0.1-SNAPSHOT.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/assignment5-0.0.1-SNAPSHOT.jar"]
