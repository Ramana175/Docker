# Docker Practical Learning

This README contains the Docker commands I practised on my AWS EC2 Ubuntu server.

Each topic includes:
- Docker command
- Simple explanation
- Real-time practical example
- Expected output wherever useful

## 1. Create a Container

A container is created from a Docker image.

**Commands:**

```bash
docker create -p 8080:80 --name nginx-web nginx
docker start nginx-web
```

**Explanation:**
- `docker create` creates a container but does not start it.
- `--name` gives a name to the container.
- `-p 8080:80` maps EC2 port 8080 to container port 80.
- `docker start` starts the created container.

**Practical example:**

First, create the Nginx container. Then start it and open:

```text
http://EC2-PUBLIC-IP:8080
```

You should see the Nginx welcome page if the port is allowed in the EC2 security group.

Another way is to create and start a container in one command:

```bash
docker run -d -p 8081:80 --name nginx-web2 nginx:1.31.8
```

Here, `docker run` creates and starts the container together.

## 2. List Containers

**Commands:**

```bash
docker ps
docker container ls
docker ps -a
docker container ls -a
```

**Explanation:**
- `docker ps` displays running containers.
- `docker container ls` does the same thing.
- `docker ps -a` displays all containers, including stopped containers.
- `docker container ls -a` is another way to display all containers.

**Practical example:**

```bash
docker ps
```

If `nginx-web2` is running, you will see it in the output.

```bash
docker ps -a
```

This also displays containers that have already stopped.

## 3. Start and Stop Containers

**Commands:**

```bash
docker start ubuntu-exec
docker stop ubuntu-exec
```

**Explanation:**
- `docker start` starts an existing stopped container.
- `docker stop` gracefully stops a running container.

**Practical example:**

If `ubuntu-exec` is stopped, start it:

```bash
docker start ubuntu-exec
docker ps
```

Now it should appear in the running container list.

Stop it:

```bash
docker stop ubuntu-exec
docker ps -a
```

Its status should show as `Exited`.

## 4. Remove a Container

**Commands:**

```bash
docker rm ubuntu-interactive
docker rm -f ubuntu-memory
```

**Explanation:**
- `docker rm` removes a stopped container.
- `docker rm -f` forcefully stops and removes a running container.

**Practical example:**

If `ubuntu-memory` is running and you want to remove it:

```bash
docker rm -f ubuntu-memory
docker ps -a
```

The container should no longer appear in the list.

## 5. Remove Multiple Containers

**Commands:**

```bash
docker rm -f container1 container2
```

Remove all running containers:

```bash
docker rm -f $(docker ps -q)
```

Remove all containers:

```bash
docker rm -f $(docker ps -aq)
```

**Explanation:**
- You can provide multiple container names or IDs.
- `docker ps -q` returns only running container IDs.
- `docker ps -aq` returns IDs of all containers.
- `$(...)` passes the command output to `docker rm`.

**Practical example:**

If you have multiple test containers running, this command removes all of them:

```bash
docker rm -f $(docker ps -q)
```

**Note:** These commands remove containers, not Docker images.

## 6. Remove Stopped Containers

**Commands:**

```bash
docker container prune
```

Remove only exited containers using IDs:

```bash
docker rm -f $(docker ps -aq --filter status=exited)
```

**Explanation:**
- `docker container prune` removes all stopped containers.
- `--filter status=exited` selects containers that have exited.
- `-a` includes all containers when listing IDs.

**Practical example:**

If you have several stopped Ubuntu test containers, run:

```bash
docker container prune
```

Docker asks for confirmation before removing them.

## 7. Rename a Container

**Command:**

```bash
docker rename ubuntu-exec ubuntu-test
```

**Explanation:**
- `docker rename` changes the name of an existing container.
- The container does not need to be recreated.

**Practical example:**

If your container name is `ubuntu-exec`, rename it to `ubuntu-test`.

Check the new name:

```bash
docker ps -a
```

You should see `ubuntu-test` instead of `ubuntu-exec`.

## 8. Restart a Container

**Command:**

```bash
docker restart ubuntu-exec
```

**Explanation:**

`docker restart` stops the container and starts it again.

**Practical example:**

If your Ubuntu container is running and you want to restart it:

```bash
docker restart ubuntu-exec
docker ps
```

The container should be running again.

## 9. Interview: Difference Between docker create and docker run

| docker create | docker run |
|---|---|
| Creates a container only | Creates and starts a container |
| Container remains stopped | Container starts immediately |
| Start separately using `docker start` | No separate start command needed |

**Example:**

```bash
docker create --name myubuntu ubuntu
docker start myubuntu
```

Using `docker run`:

```bash
docker run -dit --name myubuntu2 ubuntu
```

**Simple explanation:** `docker create` only prepares the container. `docker run` prepares and starts it.

## 10. Interview: What Is Port Mapping?

Port mapping connects a port on the EC2 host to a port inside the Docker container.

**Command:**

```bash
docker run -d -p 8081:80 --name nginx-web2 nginx:1.31.8
```

**Explanation:**

- `8081` is the EC2 host port.
- `80` is the container port.
- `-p` connects these two ports.

**Practical example:**

When you open:

```text
http://EC2-PUBLIC-IP:8081
```

The request reaches port 8081 on EC2 and Docker forwards it to port 80 inside the Nginx container.

Make sure port 8081 is allowed in your EC2 security group.

## 11. Interview: Difference Between docker stop and docker kill

**Commands:**

```bash
docker stop ubuntu-exec
docker kill ubuntu-exec
```

**Explanation:**

**docker stop**
- Sends a SIGTERM signal first.
- Gives the application time to stop gracefully.
- Sends SIGKILL if it does not stop within the grace period.

**docker kill**
- Sends SIGKILL by default.
- Stops the container immediately without the normal graceful shutdown period.

**Practical example:**

Use `docker stop` when you want an application to finish its shutdown process properly.

Use `docker kill` when you need to forcefully stop a container.

## 12. Pause and Unpause a Container

**Commands:**

```bash
docker pause ubuntu-exec
docker unpause ubuntu-exec
```

**Explanation:**
- `docker pause` temporarily freezes the processes inside a container.
- `docker unpause` allows those processes to continue.

**Practical example:**

```bash
docker pause ubuntu-exec
docker ps
```

The container status will show that it is paused.

Resume it:

```bash
docker unpause ubuntu-exec
```

The container can continue working.

## 13. Remove Paused Containers

**Command:**

```bash
docker rm -f $(docker ps -aq --filter status=paused)
```

**Explanation:**

- `--filter status=paused` selects paused containers.
- `-aq` returns their container IDs.
- `docker rm -f` forcefully removes them.

**Practical example:**

If you paused a container named `ubuntu-exec` and want to remove it, you can also use:

```bash
docker rm -f ubuntu-exec
```

This removes that specific container.

## 14. Why Does an Ubuntu Container Exit Automatically?

**Commands:**

```bash
docker run --name ubuntucontainer ubuntu
docker ps -a
docker start ubuntucontainer
```

**Explanation:**

When you run an Ubuntu image without an interactive terminal, its default command may finish immediately.

Once the main process exits, the container also exits.

`docker start` starts the same container again, but it can exit again for the same reason.

**Practical example:**

To keep an Ubuntu container running in the background:

```bash
docker run -dit --name ubuntu-exec ubuntu
```

Here:
- `-d` runs it in the background.
- `-i` keeps standard input open.
- `-t` provides a terminal.

Check it:

```bash
docker ps
```

## 15. Run a Container in Interactive Mode

**Command:**

```bash
docker run -it --name test ubuntu /bin/bash
```

**Explanation:**
- `-i` keeps input open.
- `-t` provides a terminal.
- `/bin/bash` opens the Bash shell inside Ubuntu.

**Practical example:**

After running the command, you enter the Ubuntu container.

Try:

```bash
whoami
```

Expected output:

```text
root
```

To leave the container:

```bash
exit
```

The container may stop because its main Bash process has exited.

## 16. Check Container Logs

**Commands:**

```bash
docker logs nginx-web2
docker logs --tail 10 nginx-web2
docker logs -f nginx-web2
```

**Explanation:**
- `docker logs` displays the container's logs.
- `--tail 10` displays only the last 10 lines.
- `-f` continuously follows new log output.

**Practical example:**

If your Nginx container is running, check its logs:

```bash
docker logs nginx-web2
```

To watch new log messages:

```bash
docker logs -f nginx-web2
```

Press `Ctrl + C` to stop watching logs. The container continues running.

## 17. Enter an Already Running Container

**Command:**

```bash
docker exec -it ubuntu-exec /bin/bash
```

**Explanation:**

`docker exec` runs a command inside an existing running container.

Here, it opens an interactive Bash shell.

**Practical example:**

Enter your Ubuntu container:

```bash
docker exec -it ubuntu-exec /bin/bash
```

Now run:

```bash
whoami
```

Expected output:

```text
root
```

Use `exit` to leave the container.

## 18. Execute a Command Inside a Container

**Command:**

```bash
docker exec ubuntu-exec ls
```

**Explanation:**

- `docker exec` runs a command inside a running container.
- `ls` displays files and directories.
- You do not need to enter the container's shell.

**Practical example:**

```bash
docker exec ubuntu-exec ls
```

You should see directories such as:

```text
bin
etc
home
root
usr
var
```

## 19. Check Container Processes

**Command:**

```bash
docker top ubuntu-exec
```

**Explanation:**

`docker top` displays the processes running inside a container.

**Practical example:**

```bash
docker top ubuntu-exec
```

You can check whether Bash or another application is running inside the container.

## 20. Check Container Resource Usage

**Commands:**

```bash
docker stats
docker stats ubuntu-exec
```

**Explanation:**

`docker stats` displays live resource usage, including:
- CPU usage
- Memory usage
- Network input and output
- Disk read and write information

**Practical example:**

```bash
docker stats ubuntu-exec
```

You can see how much memory and CPU your Ubuntu container is using.

Press `Ctrl + C` to stop the live display. It does not stop the container.

## 21. Set a Memory Limit for a Container

**Command:**

```bash
docker run -dit --memory="100m" --name ubuntu-memory ubuntu
```

**Explanation:**
- `--memory="100m"` limits the container to 100 MB of memory.
- `-d` runs it in the background.
- `-i` keeps input open.
- `-t` provides a terminal.
- `--name` assigns the container name.

**Practical example:**

Check the memory usage:

```bash
docker stats ubuntu-memory
```

You should see a memory limit of approximately:

```text
100MiB
```

Check whether Docker recorded an out-of-memory kill:

```bash
docker inspect --format='OOMKilled: {{.State.OOMKilled}}' ubuntu-memory
```

Example output:

```text
OOMKilled: false
```

`false` means the container has not been recorded as killed due to an out-of-memory condition.

**Important:** Setting a low memory limit does not automatically cause an OOM kill. The container must actually exceed its available memory.

## 22. Copy Files Between Host and Container

**Commands:**

Create a file on the EC2 machine:

```bash
echo "Hello Docker" > demo.txt
```

Copy the file into the container:

```bash
docker cp demo.txt ubuntu-memory:/root/
```

Check the file inside the container:

```bash
docker exec ubuntu-memory ls /root/
```

Copy the file back to the EC2 machine:

```bash
docker cp ubuntu-memory:/root/demo.txt ./demo-from-container.txt
```

**Explanation:**
- `echo` creates a simple text file.
- `docker cp` copies files between the host and a container.
- `/root/` is the destination directory inside the Ubuntu container.
- `./` represents the current directory on the EC2 machine.

**Practical example:**

You created `demo.txt` on EC2 and copied it into `ubuntu-memory`.

Inside the container, you checked that the file existed. Then you copied it back as `demo-from-container.txt`.

This is useful when you need to transfer configuration files, logs, or other files.

## 23. Interview: Difference Between COPY and docker cp

| COPY | docker cp |
|---|---|
| Used inside a Dockerfile | Used in the terminal |
| Copies files during image build | Copies files between host and container |
| Adds files to the image | Copies files to or from an existing container |

**Dockerfile example:**

```dockerfile
COPY demo.txt /root/
```

**Explanation:**

When Docker builds an image, this instruction copies `demo.txt` from the build context into `/root/` inside the image.

**Terminal example:**

```bash
docker cp demo.txt ubuntu-memory:/root/
```

This copies a file into an already-created container.

## 24. Check Changes Inside a Container

**Command:**

```bash
docker diff ubuntu-memory
```

**Explanation:**

`docker diff` displays filesystem changes made inside a container compared with its original image.

Common symbols:

| Symbol | Meaning |
|---|---|
| A | Added file or directory |
| C | Changed file or directory |
| D | Deleted file or directory |

**Practical example:**

After copying `demo.txt` into `ubuntu-memory`, you ran:

```bash
docker diff ubuntu-memory
```

Your output was:

```text
C /root
A /root/demo.txt
```

This means:
- `/root` was changed.
- `demo.txt` was added.

## 25. Create an Image from a Container

**Commands:**

```bash
docker commit ubuntu-memory ubuntu-custom:v1
docker images
```

**Explanation:**
- `docker commit` creates a new image from the current container's filesystem changes.
- `ubuntu-custom` is the image name.
- `v1` is the image tag.
- `docker images` displays the images available locally.

**Practical example:**

You copied `demo.txt` into `ubuntu-memory`.

Then you created an image:

```bash
docker commit ubuntu-memory ubuntu-custom:v1
```

Check whether the image was created:

```bash
docker images
```

You should see `ubuntu-custom` with the tag `v1`.

**Important:** `docker commit` is useful for learning or taking a quick snapshot. In real projects, Dockerfiles are generally preferred because they make image creation repeatable and easier to maintain. Changes stored in mounted volumes are not included in the committed image.

## 26. Inspect a Container

**Command:**

```bash
docker inspect ubuntu-memory
```

**Explanation:**

`docker inspect` displays detailed information about a container, such as:
- Container ID
- Container name
- Network details
- IP address
- Status
- Mounts
- Configuration

You can also use `--format` to display only the information you need.

**Example 1: Check container IP address**

```bash
docker inspect --format='{{.NetworkSettings.IPAddress}}' ubuntu-memory
```

This generic IP address field may be empty in some network configurations.

**Example 2: Check the bridge network IP**

```bash
docker inspect --format='{{.NetworkSettings.Networks.bridge.IPAddress}}' ubuntu-memory
```

Your output:

```text
172.17.0.3
```

This displays the IP address assigned to your container on the default bridge network.

**Example 3: Check container status**

```bash
docker inspect --format='{{.State.Status}}' ubuntu-memory
```

Your output:

```text
running
```

**Example 4: Display IP address and status together**

```bash
docker inspect --format='IP: {{.NetworkSettings.Networks.bridge.IPAddress}}, Status: {{.State.Status}}' ubuntu-memory
```

Your output:

```text
IP: 172.17.0.3, Status: running
```

**Simple explanation:**

Instead of reading the complete inspect output, you can use `--format` to display only the details you need.

The bridge-specific IP path works for containers connected to the default bridge network. Containers on other networks may have a different network name.

---

## Docker Commands: Quick Revision

| Command | Purpose |
|---|---|
| `docker create` | Create a container |
| `docker run` | Create and start a container |
| `docker ps` | List running containers |
| `docker ps -a` | List all containers |
| `docker start` | Start a stopped container |
| `docker stop` | Gracefully stop a container |
| `docker kill` | Forcefully stop a container |
| `docker restart` | Restart a container |
| `docker rm` | Remove a container |
| `docker rename` | Rename a container |
| `docker pause` | Pause a container |
| `docker unpause` | Resume a paused container |
| `docker logs` | View container logs |
| `docker exec` | Run a command inside a running container |
| `docker top` | View container processes |
| `docker stats` | View resource usage |
| `docker cp` | Copy files between host and container |
| `docker diff` | View filesystem changes |
| `docker commit` | Create an image from a container |
| `docker inspect` | View detailed container information |

## Best Practices

1. Give containers meaningful names using `--name`.
2. Use specific image tags instead of relying on `latest` for production deployments.
3. Use `docker logs` to troubleshoot application problems.
4. Set suitable CPU and memory limits for applications.
5. Remove unused containers to keep the environment clean.
6. Prefer Dockerfiles over `docker commit` for building repeatable images.
7. Avoid exposing unnecessary container ports publicly.
8. Keep Docker images and the Docker Engine updated using supported releases and security updates.

---
