# Docker Fundamentals & Hands-on Project

A beginner-friendly, practical guide to Docker — from understanding
containers to building and running a Java web application with Maven and
Tomcat.

> **Learning lab:** Day 1 covers Docker fundamentals and container
> basics. Day 2 covers images, Docker Hub, and deploying a Maven WAR
> application inside a Tomcat container.

------------------------------------------------------------------------

## Table of Contents

- [Learning Objectives](#learning-objectives)
- [Day 1 - Docker Fundamentals](#day-1---docker-fundamentals)
  - [What Is Docker?](#what-is-docker)
  - [Before Docker vs After Docker](#before-docker-vs-after-docker)
  - [Virtual Machines vs Containers](#virtual-machines-vs-containers)
  - [Why Docker?](#why-docker)
  - [Docker Architecture](#docker-architecture)
  - [Docker Workflow](#docker-workflow)
  - [Important Docker Terms](#important-docker-terms)
  - [Container Practical Commands](#container-practical-commands)
- [Day 2 - Images and Application
  Deployment](#day-2---images-and-application-deployment)
  - [Docker Images](#docker-images)
  - [Image Names and Tags](#image-names-and-tags)
  - [Docker Hub](#docker-hub)
  - [Save and Load Images](#save-and-load-images)
  - [Build Context and Dockerfile](#build-context-and-dockerfile)
  - [Maven WAR Deployment with
    Tomcat](#maven-war-deployment-with-tomcat)
  - [Verify the Deployment](#verify-the-deployment)
- [Docker Command Cheat Sheet](#docker-command-cheat-sheet)
- [What to Learn Next](#what-to-learn-next)

------------------------------------------------------------------------

## Learning Objectives

By completing these labs, you will understand how to:

- Explain Docker, containerization, and Docker architecture.
- Understand the difference between an image and a container.
- Run, list, inspect, stop, start, and remove containers.
- Pull, tag, build, push, save, and load Docker images.
- Write a basic Dockerfile.
- Build a Maven WAR application and package it into a Tomcat image.
- Run the application in a container and access it through a browser.

------------------------------------------------------------------------

# Day 1 - Docker Fundamentals

## What Is Docker?

**Docker is a platform for building, packaging, and running applications
in containers.**

A Docker image packages an application’s files and the runtime
components it needs. Docker uses that image to create a container.

For example, a Java web application may need Java and Tomcat. Instead of
manually setting up the same environment every time, we can package the
application with a suitable runtime image and run it as a container.

## Before Docker vs After Docker
Before Docker
An application may work on one computer but fail on another because the
software versions, libraries, or configuration are different.

With Docker
Docker packages the application environment into an image. A container
is created from that image and runs the application in an isolated
environment.

| Before container-based deployment                       | With Docker                                                         |
|---------------------------------------------------------|---------------------------------------------------------------------|
| Install and configure software directly on each server. | Package the application environment into an image.                  |
| Different servers may have different software versions. | Reuse a consistent image across environments.                       |
| Moving an application may require repeated setup.       | Transfer the image and run a container on another compatible host.  |
| Applications can conflict over installed dependencies.  | Run applications in separate containers with isolated environments. |

Docker helps improve consistency and portability. It does not remove the
need for security, configuration management, monitoring, backups, or
testing.

## Virtual Machines vs Containers

A virtual machine runs its own guest operating system on virtualized
hardware. A container shares the host operating system kernel while
keeping its processes and other resources isolated.

``` mermaid
flowchart TB
    subgraph VM["Virtual machine approach"]
        H1["Physical server"]
        V["Hypervisor"]
        H1 --> V
        V --> VM1["VM 1"]
        V --> VM2["VM 2"]
        VM1 --> G1["Guest OS"]
        VM2 --> G2["Guest OS"]
        G1 --> A1["Application"]
        G2 --> A2["Application"]
    end
```

``` mermaid
flowchart TB
    subgraph CT["Container approach"]
        H["Physical server"]
        OS["Host operating system"]
        DE["Container engine"]
        H --> OS --> DE
        DE --> C1["Container 1"]
        DE --> C2["Container 2"]
        C1 --> APP1["Application + dependencies"]
        C2 --> APP2["Application + dependencies"]
    end
```

| Virtual Machines                             | Containers                                                  |
|----------------------------------------------|-------------------------------------------------------------|
| Each VM normally has its own guest OS.       | Containers share the host kernel.                           |
| Usually use more resources.                  | Usually lightweight.                                        |
| Booting a guest OS takes longer.             | Containers generally start quickly.                         |
| Useful when a separate guest OS is required. | Useful for packaging and running applications consistently. |

Containers and VMs provide different isolation models. One is not a
universal replacement for the other.

## Why Docker?

- **Consistency:** use the same image in development, testing, and
  deployment.
- **Portability:** move images between compatible Docker environments.
- **Speed:** containers usually start quickly.
- **Efficiency:** containers share the host kernel.
- **Isolation:** run applications in separate containers.
- **Versioning:** tag images with labels such as `v1` and `v2`.
- **Repeatability:** document the image build process in a Dockerfile.

## Docker Architecture

Docker uses a client-server architecture.

``` mermaid
flowchart LR
    U["User / Developer"] --> CLI["Docker CLI"]
    CLI <-->|"Docker Engine API"| D["Docker daemon (dockerd)"]
    D --> I["Images"]
    D --> C["Containers"]
    D --> N["Networks"]
    D --> V["Volumes"]
    D <-->|"Pull / Push"| R["Docker Registry<br/>Docker Hub"]
```

**Main components**

| Component         | Simple explanation                                                         |
|-------------------|----------------------------------------------------------------------------|
| Docker Client     | The command-line tool where we type commands such as `docker run`.         |
| Docker Daemon     | Background service that manages images, containers, networks, and volumes. |
| Docker Engine API | Communication interface between the client and daemon.                     |
| Docker Image      | Template used to create containers.                                        |
| Docker Container  | An instance created from an image.                                         |
| Docker Registry   | Stores and distributes images. Docker Hub is one example.                  |

## Docker Workflow

``` mermaid
flowchart TD
    A["Write application code"] --> B["Create a Dockerfile"]
    B --> C["Build a Docker image"]
    C --> D["Run a container"]
    D --> E["Test the application"]
    E --> F["Tag the image"]
    F --> G["Push image to a registry"]
    G --> H["Pull image on another host"]
    H --> I["Run a container there"]
```

For our Maven and Tomcat lab, the workflow is:

``` mermaid
flowchart LR
    A["Java web application"] --> B["Maven build"]
    B --> C["WAR file"]
    C --> D["Dockerfile"]
    D --> E["Tomcat Docker image"]
    E --> F["Tomcat container"]
    F --> G["Browser / HTTP request"]
```

## Important Docker Terms

| Term           | Meaning                                                                 |
|----------------|-------------------------------------------------------------------------|
| Image          | A packaged template used to create containers.                          |
| Container      | A running or stopped instance created from an image.                    |
| Dockerfile     | A text file containing image build instructions.                        |
| Registry       | A service used to store and share images.                               |
| Volume         | Storage that can persist independently of a container’s writable layer. |
| Network        | Allows containers to communicate with each other and other systems.     |
| Docker Compose | A tool for defining and running multi-container applications.           |
| Tag            | A label used to identify an image version or variant.                   |

## Container Practical Commands

### Check Docker installation

``` bash
docker --version
docker version
docker info
```

| Command            | What it does                                       |
|--------------------|----------------------------------------------------|
| `docker --version` | Displays the Docker CLI version.                   |
| `docker version`   | Displays client and server version details.        |
| `docker info`      | Displays information about the Docker environment. |

### Run a container

``` bash
docker run -d --name mynginx -p 8081:80 nginx
```

| Part             | Meaning                                       |
|------------------|-----------------------------------------------|
| `docker run`     | Creates and starts a container.               |
| `-d`             | Runs it in the background.                    |
| `--name mynginx` | Gives the container a name.                   |
| `-p 8081:80`     | Maps host port `8081` to container port `80`. |
| `nginx`          | Image used to create the container.           |

**Port mapping format:**

``` text
-p HOST_PORT:CONTAINER_PORT
```

In this example, requests to port `8081` on the Docker host are
forwarded to port `80` inside the Nginx container.

### List containers

``` bash
docker ps
docker ps -a
```

- `docker ps` — lists running containers.
- `docker ps -a` — lists all containers, including stopped ones.

### Manage container lifecycle

``` bash
docker stop mynginx
docker start mynginx
docker restart mynginx
docker rm mynginx
```

| Command               | Meaning                                                            |
|-----------------------|--------------------------------------------------------------------|
| `docker stop NAME`    | Gracefully stops a running container.                              |
| `docker start NAME`   | Starts an existing stopped container.                              |
| `docker restart NAME` | Restarts an existing container.                                    |
| `docker rm NAME`      | Removes a stopped container.                                       |
| `docker rm -f NAME`   | Force-removes a container, including a running one. Use carefully. |

**Remember:** Stopping a container does not remove it. Removing a
container does not automatically remove its image.

### Inspect and troubleshoot containers

``` bash
docker logs mynginx
docker logs -f mynginx
docker inspect mynginx
docker exec -it mynginx bash
docker exec -it mynginx sh
docker stats
```

- `docker logs NAME` — displays container logs.
- `docker logs -f NAME` — follows new log output; press `Ctrl+C` to stop
  following.
- `docker inspect NAME` — displays detailed container configuration in
  JSON.
- `docker exec -it NAME bash` — opens Bash inside a running container,
  if available.
- `docker exec -it NAME sh` — opens `sh` when Bash is not available.
- `docker stats` — displays live resource usage for running containers.

### Check host ports

``` bash
sudo ss -lntp
sudo ss -lntp | grep ':8080'
curl http://localhost:8081
```

- `ss -lntp` — shows listening TCP ports and associated processes.
- `grep ':8080'` — filters the output for port 8080.
- `curl URL` — sends an HTTP request to the given URL.

------------------------------------------------------------------------

# Day 2 - Images and Application Deployment

## Docker Images

An image is a packaged template. A container is created from that image.

``` mermaid
flowchart TD
    D["Dockerfile"] -->|"docker build"| I["Docker image"]
    I -->|"docker run"| C1["Container 1"]
    I -->|"docker run"| C2["Container 2"]
    I -->|"docker run"| C3["Container 3"]
```

### Image commands

``` bash
docker images
docker image ls
docker pull nginx:latest
docker image inspect nginx:latest
docker history nginx:latest
docker rmi nginx:latest
```

| Command                      | Meaning                                              |
|------------------------------|------------------------------------------------------|
| `docker images`              | Lists images stored locally.                         |
| `docker image ls`            | Another way to list local images.                    |
| `docker pull IMAGE:TAG`      | Downloads an image from a registry.                  |
| `docker image inspect IMAGE` | Displays detailed image configuration.               |
| `docker history IMAGE`       | Shows image layers and their build history.          |
| `docker rmi IMAGE:TAG`       | Removes an image if it is not in use by a container. |

## Image Names and Tags

An image reference can include a registry, namespace, image name, and
tag.

``` text
REGISTRY/USERNAME/IMAGE_NAME:TAG
```

Example:

``` text
docker.io/ramana175/mynginx:v1
```

- `docker.io` — Docker Hub registry.
- `ramana175` — Docker Hub username or namespace.
- `mynginx` — image name.
- `v1` — tag.

You can choose your own image name and tag:

``` bash
docker build -t mytomcat-app:v1 .
docker build -t ramana-webapp:v1 .
docker build -t devops-project:1.0 .
docker build -t my-webapp:latest .
```

The image name and tag are labels you choose. They do **not** change the
software version inside the image. The Tomcat version is selected in the
Dockerfile’s `FROM` instruction.

### Tag an existing image

``` bash
docker tag nginx:latest ramana175/mynginx:v1
```

`docker tag SOURCE_IMAGE:TAG NEW_IMAGE:TAG` adds another name/tag to the
same image. It does not rebuild the image.

## Docker Hub

Docker Hub is a registry for storing and sharing Docker images.

``` mermaid
flowchart LR
    A["Local Docker image"] -->|"docker push"| H["Docker Hub"]
    H -->|"docker pull"| B["Another machine"]
```

### Login

``` bash
docker login
```

Authenticates with the registry. Use the supported login method; do not
store passwords or access tokens in Dockerfiles or source control.

### Tag and push

``` bash
docker tag nginx:latest ramana175/mynginx:v1
docker push ramana175/mynginx:v1
```

- `docker tag` — gives the image a name under your Docker Hub namespace.
- `docker push` — uploads the tagged image to Docker Hub.

### Pull an image

``` bash
docker pull ramana175/mynginx:v1
```

Downloads that image from Docker Hub.

### Logout

``` bash
docker logout
```

Ends the Docker CLI registry login session.

**Lab result:** We tagged the Nginx image as `ramana175/mynginx:v1` and
pushed it successfully to Docker Hub.

## Save and Load Images

Images can also be transferred as archive files.

``` bash
docker save -o mynginx.tar ramana175/mynginx:v1
docker load -i mynginx.tar
```

- `docker save -o FILE IMAGE` — saves an image, including its layers and
  metadata, to an archive.
- `docker load -i FILE` — loads an image archive into the local Docker
  image store.

**Difference:** `docker save` and `docker load` preserve an image.
`docker export` and `docker import` are a different workflow for
exporting and importing a container filesystem.

**Lab result:** We saved the Nginx image to `mynginx.tar`, removed local
image copies during the exercise, loaded the image again, and ran the
restored image on host port `8081`.

## Build Context and Dockerfile

### What is a build context?

The **build context** is the set of files and directories Docker can
access while building an image.

Example:

``` bash
docker build -t mytomcat-app:v1 .
```

The final `.` means the current directory is the build context. Docker
can copy files from this context using instructions such as `COPY`.

Keep the build context small. Avoid including secrets, unnecessary
files, or large local caches. A `.dockerignore` file can exclude files
from the build context.

### Common Dockerfile instructions

| Instruction  | Purpose                                                                                          |
|--------------|--------------------------------------------------------------------------------------------------|
| `FROM`       | Selects the base image.                                                                          |
| `WORKDIR`    | Sets the working directory for later instructions.                                               |
| `COPY`       | Copies files from the build context into the image.                                              |
| `RUN`        | Runs a command while building the image.                                                         |
| `ENV`        | Sets environment variables in the image.                                                         |
| `EXPOSE`     | Documents the port the application is intended to use. It does not publish the port on the host. |
| `CMD`        | Provides the default command or arguments when a container starts.                               |
| `ENTRYPOINT` | Defines the main executable for the container.                                                   |

## Maven WAR Deployment with Tomcat

In this practical, we used a Java web application from the Maven GitHub
repository.

### 1. Clone the project

``` bash
git clone https://github.com/Ramana175/Maven.git
cd Maven/tomcat-web-application
```

The repository contained these project folders:

``` text
Maven/
├── README.md
├── src/
├── standalone-application/
└── tomcat-web-application/
    ├── Jenkinsfile
    ├── pom.xml
    └── src/
```

### 2. Understand the Maven project

Important values from the project’s `pom.xml`:

| Setting               | Value                       |
|-----------------------|-----------------------------|
| `groupId`             | `com.example`               |
| `artifactId`          | `tomcat-web-application`    |
| `version`             | `1.0.0`                     |
| `packaging`           | `war`                       |
| Java compiler release | `21`                        |
| Servlet dependency    | `jakarta.servlet-api:6.0.0` |
| Dependency scope      | `provided`                  |

A **WAR (Web Application Archive)** is a package for a Java web
application. It can be deployed to a servlet container such as Tomcat.

Because this project uses Jakarta Servlet 6.0, Tomcat 10.1 is a suitable
match. Tomcat 9 uses the older `javax.servlet` namespace and is not a
direct replacement for this Jakarta-based application.

### 3. Build the WAR file

``` bash
mvn clean package
```

What it does:

- `mvn` — runs Maven.
- `clean` — removes the previous build output, usually the `target/`
  directory.
- `package` — compiles and packages the application according to the
  Maven project configuration.

Check the generated WAR:

``` bash
ls -lh target/*.war
```

Expected artifact:

``` text
target/tomcat-web-application-1.0.0.war
```

**Lab note:** The project requested Java release 21. The host initially
selected Java 25, so Maven could not compile for release 21. We
installed and selected JDK 21, checked `java -version` and
`javac -version`, and then `mvn clean package` completed successfully.

Useful checks:

``` bash
java -version
javac -version
mvn -version
```

### 4. Find the correct Tomcat deployment path

Do not guess paths inside a Docker image. Inspect the image.

We opened a temporary Tomcat container:

``` bash
docker run -it --rm tomcat:10.1 bash
```

Inside the container, we checked:

``` bash
pwd
ls
cd webapps
pwd
ls -la
```

We confirmed these paths:

``` text
Tomcat installation: /usr/local/tomcat
WAR deployment path: /usr/local/tomcat/webapps
```

Why this path? The Tomcat image uses `/usr/local/tomcat` as its
installation directory, and `webapps` is the directory where this
image’s Tomcat instance deploys web applications.

Useful commands when exploring any image:

``` bash
pwd
ls -la
find / -type d -name webapps 2>/dev/null
```

Exit the temporary container:

``` bash
exit
```

### 5. Create the Dockerfile

In the `tomcat-web-application` project directory, create a file named
`Dockerfile`.

``` dockerfile
FROM tomcat:10.1.61

COPY target/tomcat-web-application-1.0.0.war /usr/local/tomcat/webapps/tomcat-web-application.war
```

Explanation:

- `FROM tomcat:10.1.61` — uses the exact Tomcat 10.1.61 base image.
- `COPY` — copies the WAR from the Maven build output into Tomcat’s
  deployment directory inside the image.
- The destination filename `tomcat-web-application.war` gives the
  application the context path `/tomcat-web-application`.

### 6. Build the application image

Run this from the directory containing the Dockerfile:

``` bash
docker build -t mytomcat-app:v1 .
```

Explanation:

| Part           | Meaning                                          |
|----------------|--------------------------------------------------|
| `docker build` | Builds an image.                                 |
| `-t`           | Assigns an image name and tag.                   |
| `mytomcat-app` | Image name chosen for this lab.                  |
| `v1`           | Tag chosen for this build.                       |
| `.`            | Uses the current directory as the build context. |

Check the image:

``` bash
docker images
```

The image name could be different in your own project. For example,
`ramana-webapp:v1`. The Tomcat software version remains controlled by
`FROM tomcat:10.1.61`.

### 7. Check port availability

Before starting the container, we checked running containers:

``` bash
docker ps
```

We also checked whether host port 8080 was in use:

``` bash
sudo ss -lntp | grep ':8080'
```

In our lab, the Nginx container was using host port `8081`, and port
`8080` was available.

### 8. Run the Tomcat application container

``` bash
docker run -d --name maven-tomcat-app -p 8080:8080 mytomcat-app:v1
```

Explanation:

| Part                      | Meaning                                         |
|---------------------------|-------------------------------------------------|
| `docker run`              | Creates and starts a container.                 |
| `-d`                      | Runs in the background.                         |
| `--name maven-tomcat-app` | Sets the container name.                        |
| `-p 8080:8080`            | Maps EC2 host port 8080 to container port 8080. |
| `mytomcat-app:v1`         | Uses the image we built.                        |

### 9. Verify the container and deployment

Check the running container:

``` bash
docker ps
```

Check Tomcat startup and deployment logs:

``` bash
docker logs maven-tomcat-app
```

We confirmed that the logs showed:

- Apache Tomcat 10.1.61 started.
- Tomcat deployed `tomcat-web-application.war`.
- WAR deployment finished successfully.
- The HTTP connector started on port 8080.

A successful deployment log included:

``` text
Deployment of web application archive
[/usr/local/tomcat/webapps/tomcat-web-application.war]
has finished.
```

### 10. Test the application

From the EC2 host:

``` bash
curl -i http://localhost:8080/tomcat-web-application/
```

The response was:

``` text
HTTP/1.1 200
```

The returned page included:

``` html
<h1>Welcome to My Tomcat Web Application!</h1>
```

We also tested the public IP from the EC2 host:

``` bash
curl -i http://EC2-PUBLIC-IP:8080/tomcat-web-application/
```

The request returned HTTP 200.

Open the application in a browser:

``` text
http://EC2-PUBLIC-IP:8080/tomcat-web-application/
```

Replace `EC2-PUBLIC-IP` with the instance’s current public IPv4 address.

**For browser access, check all three:**

1.  The container is running and the Docker port mapping is correct.
2.  The EC2 security group allows inbound TCP port `8080` from the
    intended client IP.
3.  The browser URL uses the correct public IP, port, and context path.

### Important: EXPOSE vs -p

These are different:

- `EXPOSE 8080` in a Dockerfile documents the container port.
- `-p 8080:8080` when running the container publishes/maps the port on
  the host.

The EC2 security group is another separate network control. Docker port
mapping does not automatically open an EC2 security group rule.

### Java version inside the container

The Maven build on the EC2 host used Java 21. The Tomcat 10.1.61 image
used Java 25 in this lab. The application compiled for Java 21 ran
successfully on that newer runtime.

The Java version used to build an application and the Java runtime
inside its container are separate. For production, choose and test a
supported runtime deliberately.

------------------------------------------------------------------------

## Docker Command Cheat Sheet

| Command                         | Use                                        |
|---------------------------------|--------------------------------------------|
| `docker --version`              | Show Docker CLI version.                   |
| `docker version`                | Show client and server versions.           |
| `docker info`                   | Show Docker environment information.       |
| `docker run IMAGE`              | Create and start a container.              |
| `docker ps`                     | List running containers.                   |
| `docker ps -a`                  | List all containers.                       |
| `docker stop NAME`              | Stop a container.                          |
| `docker start NAME`             | Start a stopped container.                 |
| `docker restart NAME`           | Restart a container.                       |
| `docker rm NAME`                | Remove a stopped container.                |
| `docker logs NAME`              | Display container logs.                    |
| `docker inspect NAME`           | Show detailed container information.       |
| `docker exec -it NAME sh`       | Open a shell in a running container.       |
| `docker stats`                  | Show live container resource usage.        |
| `docker images`                 | List local images.                         |
| `docker pull IMAGE:TAG`         | Download an image.                         |
| `docker build -t NAME:TAG .`    | Build an image from the current directory. |
| `docker tag SOURCE TARGET`      | Add another image name/tag.                |
| `docker push IMAGE:TAG`         | Upload an image to a registry.             |
| `docker rmi IMAGE:TAG`          | Remove an image.                           |
| `docker save -o file.tar IMAGE` | Save an image to an archive.               |
| `docker load -i file.tar`       | Load an image archive.                     |

------------------------------------------------------------------------

## What to Learn Next

The following are the next topics in the Docker learning path:

1.  **Docker Volumes** — persist data even when containers are removed.
2.  **Docker Networks** — connect containers and understand container
    communication.
3.  **Docker Compose** — define and run multiple containers using a YAML
    file.
4.  **Advanced Dockerfiles** — practise `WORKDIR`, `RUN`, `ENV`,
    `EXPOSE`, `CMD`, `ENTRYPOINT`, and `.dockerignore`.
5.  **Multi-stage builds** — separate build and runtime stages to create
    leaner images.
6.  **Troubleshooting and cleanup** — practise logs, inspect, exec,
    resource checks, and safe cleanup.

------------------------------------------------------------------------

## Practical Lab Summary

In Day 1 and Day 2, we:

- Learned Docker fundamentals, architecture, workflow, objectives, and
  containerization.
- Compared virtual machines and containers.
- Ran Nginx and Tomcat containers.
- Practised container listing, lifecycle, logs, and inspection commands.
- Inspected, tagged, pushed, pulled, saved, and loaded Docker images.
- Cloned a Maven web application from GitHub.
- Built a WAR file using Maven.
- Created a Dockerfile using Tomcat 10.1.61.
- Built the image `mytomcat-app:v1`.
- Started the container `maven-tomcat-app` on host port 8080.
- Verified Tomcat deployment logs and received HTTP 200 from the
  application.

**End of Day 1 and Day 2.**
