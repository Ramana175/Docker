# Docker Best Practices

Docker best practices help us build images that are secure, lightweight, easy to maintain, and efficient.

## 1. Use Official Base Images

Use trusted and official images whenever possible.

**Example:**
```dockerfile
FROM ubuntu:24.04
```

**Why?**
- Official images are maintained by trusted publishers.
- They are more reliable.
- They reduce security risks.

## 2. Use Specific Image Tags

Avoid using `latest` in production.

**Not recommended:**
```dockerfile
FROM nginx:latest
```

**Recommended:**
```dockerfile
FROM nginx:1.31.8
```

**Why?**

A specific tag helps us use a predictable version. The `latest` tag can point to a newer version when we pull the image again.

## 3. Use a .dockerignore File

A `.dockerignore` file prevents unnecessary files from being sent to the Docker build context.

**Example `.dockerignore`:**
```text
*.log
*.tmp
.env
.git
node_modules
```

**Why?**
- Reduces build context size.
- Avoids copying unnecessary files.
- Helps prevent sensitive files from entering the build context.

## 4. Prefer COPY for Normal File Copying

Use `COPY` when you only need to copy files into an image.

**Example:**
```dockerfile
FROM ubuntu:24.04
WORKDIR /app
COPY index.html /app/
```

**Why?**

`COPY` has a clear purpose: copying files and directories from the build context into the image.

Use `ADD` when you specifically need its additional features, such as extracting a local TAR archive.

## 5. Use Multi-Stage Builds

Multi-stage builds separate the application build environment from the final runtime environment.

**Example:**
```dockerfile
# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Run
FROM tomcat:10.1.61

COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/
```

**Why?**
- Build tools are not required in the final runtime image.
- Keeps the final image cleaner.
- Reduces unnecessary files in the runtime environment.

## 6. Run Containers as a Non-Root User

Avoid running applications as root when it is not required.

**Example:**
```dockerfile
FROM ubuntu:24.04

RUN useradd -m appuser

USER appuser
```

**Why?**

If an application is compromised, running it as a non-root user can reduce the damage an attacker can cause.

## 7. Use Alpine Images When Suitable

Alpine images are lightweight compared with many general-purpose Linux images.

**Example:**
```dockerfile
FROM alpine:3.22

RUN apk add --no-cache curl
```

**Why?**
- Smaller image size.
- Fewer unnecessary packages.
- Useful for applications that support Alpine.

Note: Alpine is not suitable for every application. Check application compatibility before using it.

## 8. Use Docker Volumes for Persistent Data

Use volumes when application data must remain available even after a container is removed.

**Create a volume:**
```bash
docker volume create mydata
```

**Use it with a container:**
```bash
docker run -d \
  --name mynginx \
  -v mydata:/usr/share/nginx/html \
  nginx:1.31.8
```

**Why?**

Container writable data is removed with the container, but a separately managed Docker volume can preserve data.

## 9. Use Docker Networks for Container Communication

Use user-defined bridge networks when containers need to communicate with each other.

**Create a network:**
```bash
docker network create mynetwork
```

**Run containers in that network:**
```bash
docker run -d --name app1 --network mynetwork nginx:1.31.8

docker run -d --name app2 --network mynetwork nginx:1.31.8
```

**Why?**

Containers connected to the same user-defined bridge network can communicate using container names instead of relying on IP addresses.

## 10. Avoid Unnecessary Containers and Images

Remove containers and images that are no longer needed.

**Check Docker disk usage:**
```bash
docker system df
```

**List dangling images:**
```bash
docker image ls -f dangling=true
```

**Remove dangling images:**
```bash
docker image prune
```

**Remove stopped containers:**
```bash
docker container prune
```

**Why?**

Regular cleanup helps manage disk space. Before deleting resources, check whether you still need them.

## 11. Do Not Store Secrets in Dockerfiles

Avoid writing passwords, tokens, or API keys directly inside Dockerfiles.

**Not recommended:**
```dockerfile
ENV DB_PASSWORD=mysecretpassword
```

**Better approach:**

Use Docker secrets or an appropriate secret-management solution for sensitive information.

**Why?**

Secrets written into image layers or environment configuration can be exposed to people who can inspect the image or container.

## 12. Use HEALTHCHECK When Required

A health check helps Docker determine whether an application is healthy.

**Example:**
```dockerfile
FROM nginx:1.31.8

HEALTHCHECK --interval=30s --timeout=5s \
  CMD curl -f http://localhost/ || exit 1
```

**Why?**

It allows Docker to report whether the container is healthy or unhealthy. A health check does not automatically restart an unhealthy container.

## 13. Monitor Container Logs

Check application logs when troubleshooting.

**Command:**
```bash
docker logs mynginx
```

Follow logs continuously:
```bash
docker logs -f mynginx
```

**Why?**

Logs help us identify application errors and understand what is happening inside a container.

---

## Summary

Important Docker best practices:

1. Use official base images.
2. Use specific image tags.
3. Maintain a `.dockerignore` file.
4. Prefer `COPY` for normal file copying.
5. Use multi-stage builds.
6. Run containers as non-root users.
7. Use Alpine when suitable.
8. Use volumes for persistent data.
9. Use custom networks for container communication.
10. Clean up unused Docker resources carefully.
11. Never hardcode secrets in Dockerfiles.
12. Use health checks when required.
13. Monitor container logs.

**Conclusion:**

Following Docker best practices helps us build secure, lightweight, reliable, and maintainable containerized applications.