FROM maven:3.6.2-jdk-8-slim

COPY mvnw .
# COPY .mvn .mvn

COPY pom.xml .

COPY src src

ARG JAR_FILE=target/MoneyMovements-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} moneyMovements.jar

CMD ["java","-jar","moneyMovements.jar"]