# Jenkins

## Debug image generated for build by Jenkins

```bash
export DOCKER_BUILDKIT=1 && docker build -t jenkins_java17 -f ./Dockerfile .
```
- Run the image as container:
```bash
docker run --name jenkins_java17 -v /root/.m2:/root/.m2 -v /var/run/docker.sock:/var/run/docker.sock -v $(pwd)/../:/var/lib/secure-api-spring -w /var/lib/secure-api-spring -d jenkins_java17 sleep infinity
```
- Enter inside the container using the image ID returned by the previous command:
```bash
docker exec -it <Image ID> /bin/bash
```
- Execute a maven command:
```bash
./mvnw clean test
```

## Stop the container after finish

```bash
docker container rm -f <Image ID>
```