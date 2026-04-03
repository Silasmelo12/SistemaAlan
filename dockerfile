# --- build ---
    FROM eclipse-temurin:25-jdk-alpine AS build
    WORKDIR /app
    
    COPY mvnw .
    COPY .mvn .mvn
    COPY pom.xml .
    COPY src src
    
    RUN chmod +x mvnw && ./mvnw -B -DskipTests clean package
    
    # --- run ---
    FROM eclipse-temurin:25-jre-alpine
    WORKDIR /app
    
    COPY --from=build /app/target/*.jar app.jar
    
    ENV PORT=8080
    EXPOSE 8080
    
    USER nobody
    ENTRYPOINT ["sh", "-c", "exec java -jar -Dserver.port=${PORT} app.jar"]