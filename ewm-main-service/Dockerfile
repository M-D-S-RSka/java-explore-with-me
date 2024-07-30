FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar main.jar
ENTRYPOINT ["java","-jar","/main.jar"]