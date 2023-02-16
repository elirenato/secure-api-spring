FROM ubuntu:20.04
ARG DEBIAN_FRONTEND=noninteractive
RUN apt-get update && \
  apt-get install wget build-essential libz-dev -y
RUN cd /opt && \
    wget "https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-22.1.0/graalvm-ce-java17-linux-amd64-22.1.0.tar.gz" && \
    tar -xvf graalvm-ce-java17-linux-amd64-22.1.0.tar.gz && \
    rm -rf graalvm-ce-java17-linux-amd64-22.1.0.tar.gz && \
    export PATH=/opt/graalvm-ce-java17-22.1.0/bin:$PATH && \
    export JAVA_HOME=/opt/graalvm-ce-java17-22.1.0 && \
    echo "export PATH=/opt/graalvm-ce-java17-22.1.0/bin:$PATH" >> /etc/profile && \
    echo "export JAVA_HOME=/opt/graalvm-ce-java17-22.1.0" >> /etc/profile
RUN apt-get install maven -y
RUN apt install -y apt-transport-https ca-certificates curl gnupg-agent software-properties-common && \
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add - && \
    add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" && \
    apt update -y && \
    apt install docker-ce -y
