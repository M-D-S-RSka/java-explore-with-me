FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar stat.jar
ENTRYPOINT ["java","-jar","/stat.jar"]