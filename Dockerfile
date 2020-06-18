FROM maven:3.6.2-jdk-8-slim

RUN adduser moneybox
USER moneybox

COPY mvnw .
# COPY .mvn .mvn

COPY pom.xml .

COPY src src

# RUN mvn package

ARG JAR_FILE=target/MoneyMovements-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} moneyMovements.jar

COPY wait-for-it.sh /wait-for-it.sh

# CMD ["java","-jar","moneyMovements.jar"]
ENTRYPOINT ["./wait-for-it.sh","mysql:3306","--","java","-jar","moneyMovements.jar"]
