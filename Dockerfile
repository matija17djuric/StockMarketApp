FROM eclipse-temurin:24-jdk

WORKDIR /app

COPY *.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]