FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-alpine
COPY --from=build /target/*.jar ./
RUN mv *.jar noteapp.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","noteapp.jar"]
