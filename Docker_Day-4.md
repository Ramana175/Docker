# Dockerfile Learning – Complete Practical Guide

## 1. What is a Dockerfile?

A Dockerfile is a text file containing instructions to build a Docker image.

Instead of manually installing and configuring everything inside a container, we write the instructions in a Dockerfile.

Docker reads these instructions from top to bottom and creates an image.

**Simple example:**

```dockerfile
FROM ubuntu:24.04

RUN echo "Hello from Docker"
```

Here:
- `FROM` selects the base image.
- `RUN` executes a command while building the image.

## 2. Basic Dockerfile Workflow

```text
Dockerfile
    |
    v
docker build
    |
    v
Docker Image
    |
    v
docker run
    |
    v
Container
```

**In simple words:**

1. Write instructions in a Dockerfile.
2. Build an image using `docker build`.
3. Start a container using `docker run`.

## 3. Dockerfile Instructions

### 3.1 FROM

`FROM` specifies the base image.

Example:

```dockerfile
FROM ubuntu:24.04
```

Explanation:
- `FROM` tells Docker which image to use as the foundation.
- `ubuntu:24.04` is the base image.

**Real-time example:** If we want an Ubuntu-based application environment, we start with an Ubuntu image.

### 3.2 LABEL

`LABEL` adds information or metadata to an image.

Example:

```dockerfile
LABEL author="Docker Practice"
LABEL version="1.0"
LABEL description="My Dockerfile learning"
```

We can use labels to identify:
- Image author
- Application version
- Project description

**Remember:** `MAINTAINER` is an older, deprecated instruction. Use `LABEL` instead.

### 3.3 COPY

`COPY` copies files from our local build directory into the image.

Example:

```dockerfile
COPY demo.txt /opt/
```

Explanation:
- `demo.txt` is the source file.
- `/opt/` is the destination inside the image.

**Practical command:**

```bash
docker run --rm firstimage:v3 ls /opt/
```

This displays the files inside `/opt/`.

### 3.4 ADD

`ADD` also copies files into an image.

Example:

```dockerfile
ADD add-demo.txt /opt/
```

**Difference between COPY and ADD:**

| COPY | ADD |
|---|---|
| Copies files and directories | Copies files and directories |
| Simple copying | Has additional features |
| Does not automatically extract TAR archives | Can automatically extract local TAR archives |
| Preferred for normal file copying | Use when its extra features are needed |

**Best practice:** Prefer `COPY` for normal file copying.

### 3.5 RUN

`RUN` executes commands while building the image.

Example:

```dockerfile
RUN echo "Hello from RUN instruction"
```

Explanation:

This command executes during `docker build`, not when we normally start the container.

We can also install software using RUN.

Example:

```dockerfile
RUN apt-get update
```

### 3.6 CMD

`CMD` specifies the default command or arguments when a container starts.

Example:

```dockerfile
CMD ["echo", "Hello from CMD instruction"]
```

When we start the container:

```bash
docker run firstimage:v7
```

Output:

```text
Hello from CMD instruction
```

**Important:** A command supplied after the image name can replace the default CMD.

Example:

```bash
docker run firstimage:v7 echo "Hello from Docker run"
```

### 3.7 ENTRYPOINT

`ENTRYPOINT` specifies the main executable for a container.

Example:

```dockerfile
ENTRYPOINT ["echo", "Hello from ENTRYPOINT"]
```

It is useful when we want the container to run a particular application or command.

### 3.8 CMD vs ENTRYPOINT

| CMD | ENTRYPOINT |
|---|---|
| Provides a default command or arguments | Defines the main executable |
| Can be replaced by a command supplied to `docker run` | Remains in place unless explicitly overridden |
| Often used for default behavior | Often used for the main application |

**Using both together:**

```dockerfile
ENTRYPOINT ["echo", "Hello from ENTRYPOINT"]

CMD ["Hello from CMD"]
```

With exec-form ENTRYPOINT, CMD supplies default arguments.

### 3.9 Shell Form vs Exec Form

There are two ways to write CMD and ENTRYPOINT instructions.

**Shell form:**

```dockerfile
CMD echo "Hello Docker"
```

**Exec form:**

```dockerfile
CMD ["echo", "Hello Docker"]
```

| Shell form | Exec form |
|---|---|
| Uses a shell | Runs the executable directly |
| Supports shell features | Does not automatically use a shell |
| Can introduce shell signal-handling issues | Generally preferred for CMD and ENTRYPOINT |

**Best practice:** Prefer exec form for CMD and ENTRYPOINT.

### 3.10 COPY vs docker cp

Both are used to transfer files, but they work differently.

**COPY example:**

```dockerfile
COPY demo.txt /opt/
```

Copies a file into the image during image building.

**docker cp example:**

```bash
docker cp cp-demo.txt cp-test:/tmp/
```

Copies a file from the host into an existing container.

| COPY | docker cp |
|---|---|
| Used in Dockerfile | Used in terminal |
| Works during image building | Works with an existing container |
| Requires rebuilding the image to include a changed file | No image rebuild required |

### 3.11 ARG

`ARG` creates a variable available during image building.

Example:

```dockerfile
FROM ubuntu:24.04

ARG APP_VERSION=1.0

RUN echo "Building version $APP_VERSION"
```

Build command:

```bash
docker build -t myapp:v1 --build-arg APP_VERSION=2.0 .
```

Here, we pass `2.0` as the build-time value.

### 3.12 ENV

`ENV` creates an environment variable that is available inside the image and running container.

Example:

```dockerfile
FROM ubuntu:24.04

ENV APP_NAME=development

CMD ["sh", "-c", "echo Hello from $APP_NAME"]
```

Build:

```bash
docker build -t firstimage:v10 .
```

Run:

```bash
docker run --rm firstimage:v10
```

Output:

```text
Hello from development
```

**Change the value without rebuilding:**

```bash
docker run --rm -e APP_NAME=production firstimage:v10
```

Output:

```text
Hello from production
```

Explanation:
- `ENV` sets the default value.
- `-e` overrides the value when starting a container.

**Important:** Never store passwords, tokens, or secrets in ARG or ENV. Use appropriate secret-management features instead.

### 3.13 WORKDIR

`WORKDIR` sets the working directory inside the container.

Example:

```dockerfile
FROM ubuntu:24.04

WORKDIR /app
```

Build:

```bash
docker build -t firstimage:v11 .
```

Verify:

```bash
docker run --rm firstimage:v11 pwd
```

Output:

```text
/app
```

Explanation:

- Docker creates `/app` if it does not exist.
- Later instructions use this directory as their working directory unless changed.

### 3.14 EXPOSE

`EXPOSE` documents the port that an application inside the container is expected to use.

Example:

```dockerfile
EXPOSE 8080
```

Verify:

```bash
docker image inspect firstimage:v12
```

Look for:

```text
"ExposedPorts": {
    "8080/tcp": {}
}
```

**Important difference:**

`EXPOSE` does not publish a port to the host.

To publish a port, use `-p`:

```bash
docker run -d -p 8080:8080 myimage
```

Here:
- First `8080` = host port.
- Second `8080` = container port.

### 3.15 VOLUME

`VOLUME` declares a location for persistent data.

Example:

```dockerfile
VOLUME ["/data"]
```

Docker creates a volume mount point for `/data`.

**Why do we use VOLUME?**

Suppose an application stores uploaded files inside `/data`. We want that data to remain available even if the container is removed.

### VOLUME Practical

Create a container:

```bash
docker run -dit --name volume-test firstimage:v13
```

Create a file inside the volume:

```bash
docker exec volume-test sh -c 'echo "My first Docker volume data" > /data/myfile.txt'
```

Read the file:

```bash
docker exec volume-test cat /data/myfile.txt
```

Output:

```text
My first Docker volume data
```

Remove the container:

```bash
docker rm -f volume-test
```

List volumes:

```bash
docker volume ls
```

The volume still exists after removing the container.

**Important:** A volume has a lifecycle separate from its container. Removing a container does not normally remove its volume. You can also attach an existing volume to another container.

### 3.16 USER

`USER` specifies which Linux user runs subsequent Dockerfile instructions and the container's default process.

Example:

```dockerfile
FROM ubuntu:24.04

RUN useradd -m appuser

USER appuser
```

Explanation:

- `RUN useradd -m appuser` creates a Linux user named `appuser`.
- `-m` creates the user's home directory.
- `USER appuser` switches to that user.

Verify:

```bash
docker run --rm firstimage:v14 whoami
```

Output:

```text
appuser
```

**Best practice:** Run applications as a non-root user whenever possible. This improves container security.

## 4. Important Docker Commands

### Build an image

```bash
docker build -t myimage:v1 .
```

Builds an image using the Dockerfile in the current directory.

### List images

```bash
docker images
```

Displays available Docker images.

### Run a container

```bash
docker run -d --name mycontainer myimage:v1
```

Creates and starts a container in detached mode.

### List running containers

```bash
docker ps
```

Displays running containers.

### List all containers

```bash
docker ps -a
```

Displays running and stopped containers.

### Inspect an image

```bash
docker image inspect myimage:v1
```

Displays detailed image information.

### Inspect a container

```bash
docker inspect mycontainer
```

Displays detailed container information.

### List volumes

```bash
docker volume ls
```

Displays Docker volumes.

## 5. Important Differences to Remember

| Topic | Meaning |
|---|---|
| Dockerfile | Contains image-building instructions |
| Image | Blueprint used to create containers |
| Container | Running or stopped instance of an image |
| RUN | Executes during image building |
| CMD | Provides default command or arguments |
| ENTRYPOINT | Defines the main executable |
| ARG | Build-time variable |
| ENV | Environment variable available at runtime |
| WORKDIR | Sets the working directory |
| EXPOSE | Documents the container port |
| VOLUME | Declares a data mount point |
| USER | Sets the user for subsequent instructions and default process |

## 6. Docker Build Cache

Docker can reuse previously built layers when instructions and their dependencies have not changed.

Example:

```text
Step 1/3 : FROM ubuntu:24.04
Step 2/3 : WORKDIR /app
 ---> Using cache
Step 3/3 : EXPOSE 8080
```

`Using cache` means Docker reused an existing layer instead of rebuilding it.

**Why is this useful?**

- Saves build time.
- Avoids repeating unchanged work.
- Makes image building faster.

## 7. Dockerfile Best Practices

1. Choose a suitable and trusted base image.
2. Prefer specific image tags instead of relying on `latest`.
3. Use `COPY` for normal file copying.
4. Prefer exec form for CMD and ENTRYPOINT.
5. Use WORKDIR to organize application files.
6. Avoid running applications as root when possible.
7. Use VOLUME when data needs to persist separately from the container.
8. Do not store secrets in Dockerfile instructions or image environment variables.
9. Keep images small and avoid unnecessary files.
10. Use Docker build cache effectively.

## 8. My Practical Learning Progress

I practised these topics on my AWS EC2 Ubuntu machine.

| Image version | Topic practised |
|---|---|
| `firstimage:v10` | ENV |
| `firstimage:v11` | WORKDIR |
| `firstimage:v12` | EXPOSE |
| `firstimage:v13` | VOLUME |
| `firstimage:v14` | USER |

I also practised:
- FROM
- LABEL
- COPY
- ADD
- RUN
- CMD
- ENTRYPOINT
- Shell form and Exec form
- COPY vs docker cp
- ARG and ENV differences
- Docker image building and verification
- Docker volume creation and data persistence

## 9. Final Understanding

A Dockerfile helps us automate the process of creating Docker images.

We write instructions once, build an image, and use that image to create containers.

Learning Dockerfile instructions is important for real-time DevOps work because applications are commonly packaged into Docker images before being deployed to servers or cloud environments.

**My goal:** Understand each instruction, practise it on AWS EC2, and use these concepts in real-time DevOps projects.