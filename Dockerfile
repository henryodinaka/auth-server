FROM maven:3.6.1-jdk-11-slim AS build
RUN mkdir -p /workspace
WORKDIR /workspace
COPY pom.xml /workspace

ENV KEY_FRONT stillkeepitsimpleAtTheBackEnd
ENV KEY_BACK stillkeepitsimpleAtTheBackEnd
ENV PROJECT_ID trusty-dialect-284219
COPY src /workspace/src
RUN mvn -B clean package --file pom.xml -Dspring.profiles.active=staging -DskipTests

FROM openjdk:14-slim
COPY --from=build /workspace/target/*auth-serve.jar auth-serve.jar
EXPOSE 80
VOLUME /usr/src
ENTRYPOINT ["java","-Dspring.profiles.active=staging","-jar","auth-serve.jar"]