# ─────────────────────────────────────────────────────
# STAGE 1: BUILD
# Χρησιμοποιούμε image που έχει Maven + Java 8
# Δουλειά: κάνει compile τον κώδικα και παράγει το .jar
# ─────────────────────────────────────────────────────
FROM maven:3.8.6-openjdk-8 AS build

# Ορίζουμε φάκελο εργασίας μέσα στο container
WORKDIR /app

# Αντιγράφουμε ΜΟΝΟ το pom.xml πρώτα.
# Λόγος: το Docker κάνει cache κάθε βήμα.
# Αν το pom.xml δεν άλλαξε, δεν κατεβάζει ξανά τα dependencies.
# Αυτό κάνει τα επόμενα builds πολύ πιο γρήγορα.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Τώρα αντιγράφουμε τον υπόλοιπο κώδικα
COPY src ./src

# Κάνουμε build — παράγει το .jar στο /app/target/
# -DskipTests: παρακάμπτουμε τα tests για ταχύτητα
RUN mvn package -DskipTests

# ─────────────────────────────────────────────────────
# STAGE 2: RUN
# Παίρνουμε ελαφριά image με μόνο Java 8 runtime (χωρίς Maven)
# Δουλειά: τρέχει το .jar που φτιάξαμε παραπάνω
# ─────────────────────────────────────────────────────
FROM eclipse-temurin:8-jre-alpine

WORKDIR /app

# Αντιγράφουμε ΜΟΝΟ το .jar από το stage 1
# Το Maven, ο source code, τα dependencies — τίποτα άλλο δεν μπαίνει εδώ
COPY --from=build /app/target/*.jar app.jar

# Λέμε στο Docker ότι αυτό το container ακούει στην πόρτα 8080
EXPOSE 8080

# Η εντολή που τρέχει όταν ξεκινά το container
ENTRYPOINT ["java", "-jar", "app.jar"]
