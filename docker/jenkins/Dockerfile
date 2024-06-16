FROM debian:stable-slim

ARG DEBIAN_FRONTEND=noninteractive

# https://docs.docker.com/engine/install/debian/
# Add Docker's official GPG key:
RUN apt update
RUN apt install -y ca-certificates curl
RUN install -m 0755 -d /etc/apt/keyrings
RUN curl -fsSL https://download.docker.com/linux/debian/gpg -o /etc/apt/keyrings/docker.asc
Run chmod a+r /etc/apt/keyrings/docker.asc
# Add the repository to Apt sources:
RUN echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/debian \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  tee /etc/apt/sources.list.d/docker.list > /dev/null
RUN apt update
RUN apt -y install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

ENV DOCKER_BUILDKIT=1

# JVM (GraalVM to use native image in the future)
RUN cd /opt && \
    curl -L -O "https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-23.0.0/graalvm-community-jdk-23.0.0_linux-x64_bin.tar.gz" && \
    tar -xvf graalvm-community-jdk-23.0.0_linux-x64_bin.tar.gz && \
    rm -rf graalvm-community-jdk-23.0.0_linux-x64_bin.tar.gz

ENV PATH="${PATH}:/opt/graalvm-community-openjdk-23+37.1/bin"
ENV JAVA_HOME="/opt/graalvm-community-openjdk-23+37.1"

# kubectl to deploy on K8s
RUN curl -L -O "https://s3.us-west-2.amazonaws.com/amazon-eks/1.29.8/2024-09-11/bin/linux/amd64/kubectl" && \
    chmod ugo+x ./kubectl && \
    mkdir -p /usr/local/bin && cp ./kubectl /usr/local/bin/kubectl

# install envsubst command of the gettext package
RUN apt install -y gettext

# Clean up, try to reduce image size
RUN apt autoremove -y \
    && apt clean all \
    && rm -rf /var/lib/apt/lists/* \
    && rm -rf /usr/share/doc /usr/share/man /usr/share/locale \
    && rm -f /usr/local/etc/php-fpm.d/*.conf \
    && rm -rf /usr/src/php \
    && rm -rf /var/tmp/* /usr/share/doc/*

WORKDIR /opt/secure-api-spring
