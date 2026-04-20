# STEP 1: Build Base with Dependencies Cached
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Cache Parent POM dependencies
COPY be/pom.xml be/pom.xml
RUN mvn install -N -f be/pom.xml

# Cache Common-Lib dependencies
COPY be/common-lib/pom.xml be/common-lib/pom.xml
RUN mvn dependency:go-offline -f be/common-lib/pom.xml

# Build Common-Lib
COPY be/common-lib/src be/common-lib/src
RUN mvn install -DskipTests -f be/common-lib/pom.xml

# Build the Service
COPY be/services/product-catalog-service/pom.xml be/services/product-catalog-service/pom.xml
RUN mvn dependency:go-offline -f be/services/product-catalog-service/pom.xml

COPY be/services/product-catalog-service/src be/services/product-catalog-service/src
WORKDIR /app/be/services/product-catalog-service
RUN mvn package -DskipTests

# STEP 2: Run Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/be/services/product-catalog-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
