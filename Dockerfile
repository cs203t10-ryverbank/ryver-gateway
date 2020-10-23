FROM openjdk:11-jre
WORKDIR /ryver-gateway
COPY . /app
ENTRYPOINT ["java","-jar","/app/ryver-gateway/target/gateway-0.0.1-SNAPSHOT.jar"]