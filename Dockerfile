FROM openjdk:17-alpine
COPY ./target/*.jar ./
RUN mv *.jar noteapp.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","noteapp.jar"]
