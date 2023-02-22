FROM ubuntu:20.04
ARG DEBIAN_FRONTEND=noninteractive
RUN apt-get update && \
  apt-get install wget build-essential libz-dev -y
RUN cd /opt && \
    wget "https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-22.1.0/graalvm-ce-java17-linux-amd64-22.1.0.tar.gz" && \
    tar -xvf graalvm-ce-java17-linux-amd64-22.1.0.tar.gz && \
    rm -rf graalvm-ce-java17-linux-amd64-22.1.0.tar.gz
ENV PATH="${PATH}:/opt/graalvm-ce-java17-22.1.0/bin"
ENV JAVA_HOME="/opt/graalvm-ce-java17-22.1.0"
COPY ./target/secure-api-spring-0.0.1-SNAPSHOT.jar secure-api-spring-0.0.1-SNAPSHOT.jar
CMD ["java","-jar","/secure-api-spring-0.0.1-SNAPSHOT.jar"]
