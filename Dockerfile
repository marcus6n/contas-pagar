# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar arquivos de dependências primeiro (cache)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fonte e buildar
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Criar usuário não-root
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copiar JAR do stage de build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
