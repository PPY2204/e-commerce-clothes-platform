# Global Build Stage
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
# Context is the project root
COPY be be
RUN mvn install -pl common-lib -DskipTests -f be/pom.xml

RUN mvn clean package -pl services/user-service -am -DskipTests -f be/pom.xml

# Run Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/be/services/user-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
