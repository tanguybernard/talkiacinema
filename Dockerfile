# --- Étape 1 : Build (Compilation avec Maven) ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# On copie le pom.xml et on télécharge les dépendances (mise en cache Docker)
COPY pom.xml .
RUN mvn dependency:go-offline

# On copie les sources Kotlin
COPY src src

# On compile le projet et on crée le JAR (en sautant les tests pour aller plus vite)
RUN mvn clean package -DskipTests

# --- Étape 2 : Run (Image légère pour l'exécution) ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# On récupère le fichier JAR généré à l'étape précédente
# Le nom peut varier, on prend n'importe quel fichier .jar dans target
COPY --from=build /app/target/*.jar app.jar

# On expose le port 9090 (purement informatif pour Docker)
EXPOSE 9090

# Commande de lancement
ENTRYPOINT ["java", "-Xmx350m", "-jar", "app.jar", "--server.port=${PORT:9090}"]