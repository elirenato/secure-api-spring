## Jenkins

### Required plugins

- Docker Pipeline.
- Docker Commons.
- JaCoCo.
- SSH Agent.

### Required credentials (Manage Jenkins -> Credentials)

- Create a credential of type `Username with password` named `docker-credentials` using your Docker Hub login details.
- Create a credential of type `Secret file` named `kube-config`, using your kube config file.

Note: You can retrieve the kube config file by running the command `microk8s config`.

### Required environment variables (Manage Jenkins -> System -> Global properties)

- DOCKER_REGISTRY_NAMESPACE:  When deploying the built image to Docker Hub (publicly accessible), create this environment variable and set it to your Docker username.

### Pipeline

Create a New Item of type `Pipeline Multibranch`.

- In the `Branch Sources` section, select GitHub as the source.
- In the Behaviors section, add the option `Filter by name (with regular expression)` and use `^(main|develop)` as the regular expression.
- Set the `Script Path` to `customer-svc/src/main/jenkins/Jenkinsfile`.

See the [Jenkinsfile](./Jenkinsfile) for more details.

### How to debug/test the Jenkins agent image

In the root directory of the repository, run the following command:

`./task.sh docker:jenkins:up`.

This will build the Jenkins Agent image and run it as a container. To access the container, use the following command:

`./task.sh docker:jenkins:bash`.

### Troubleshooting - Error can not connect to Ryuk at 172.17.0.1:32768

When building the project on the Jenkins server, if you encounter the following error during the Jenkins build:

```bash
04:44:03.434 [main] INFO tc.testcontainers/ryuk:0.7.0 -- Container testcontainers/ryuk:0.7.0 started in PT0.718419967S
04:44:08.444 [testcontainers-ryuk] WARN org.testcontainers.utility.RyukResourceReaper -- Can not connect to Ryuk at 172.17.0.1:32768
java.net.SocketTimeoutException: Connect timed out
...
```

Make sure to allow the IP (Docker network) 172.17.0.1 into your firewall, e.g.:

`ufw allow from 172.17.0.0/16`
