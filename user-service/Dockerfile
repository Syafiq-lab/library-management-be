FROM maven:3.9-eclipse-temurin-21 AS dev
WORKDIR /workspace
CMD ["mvn","-q","-DskipTests","clean","spring-boot:run"]

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre AS prod
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
