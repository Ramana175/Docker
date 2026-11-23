# Docker Networking and Volumes

This document covers Docker Networking and Docker Volumes with simple explanations and practical examples performed on an Ubuntu EC2 instance.

# Part 1: Docker Networking

## 1. What is Docker Networking?

Docker networking allows containers to communicate with:

- Other containers
- The host machine
- External networks and the internet

**Simple example:**

Imagine we have two containers:

- One container runs a Java application.
- Another container runs MongoDB.

The Java application needs to communicate with MongoDB. Docker networking helps these containers communicate with each other.

## 2. Docker Network Types

Docker provides different network drivers:

1. Bridge
2. Host
3. None
4. Overlay

## 3. Bridge Network

The bridge network is the default network driver in Docker.

When we create a container without specifying a network, Docker connects it to the default bridge network.

### Practical: Default Bridge Network

Run two containers:

```bash
docker run -dit --name cthree ubuntu:24.04
docker run -dit --name cone ubuntu:24.04
```

Check running containers:

```bash
docker ps
```

Check their IP addresses:

```bash
docker inspect cthree
docker inspect cone
```

We observed container IP addresses such as:

```text
cthree - 172.17.0.2
cone   - 172.17.0.3
```

Test communication using an IP address:

```bash
docker exec -it cthree ping 172.17.0.3
```

**Important:** Containers connected to the default bridge can communicate using IP addresses, but automatic container-name DNS resolution is not provided in the same way as a user-defined bridge.

## 4. Custom Bridge Network

A custom bridge network is a user-created network.

It provides automatic DNS resolution using container names.

### Step 1: Create a Network

```bash
docker network create jionetwork
```

### Step 2: List Networks

```bash
docker network ls
```

Displays all Docker networks.

### Step 3: Inspect Network

```bash
docker network inspect jionetwork
```

Displays network details such as subnet, gateway and connected containers.

We observed:

```text
Subnet: 172.18.0.0/16
Gateway: 172.18.0.1
```

### Step 4: Create Containers Using Custom Network

```bash
docker run -dit --name cone --network jionetwork ubuntu:24.04
docker run -dit --name ctwo --network jionetwork ubuntu:24.04
```

### Step 5: Test Communication Using Container Name

```bash
docker exec -it cone ping ctwo
```

**Result:** The containers successfully communicated using the container name.

This is one of the main advantages of a user-defined bridge network.

## 5. Connect and Disconnect Containers from Networks

We can connect an existing container to another network.

### Connect

```bash
docker network connect jionetwork cone
```

Connects the container `cone` to `jionetwork`.

### Disconnect

```bash
docker network disconnect jionetwork cone
```

Disconnects the container from that network.

### Inspect Container Networks

```bash
docker inspect cone
```

Check the `Networks` section to see which networks are connected.

## 6. Host Network

Host networking allows a container to use the host machine's network directly.

It does not get a separate container network namespace in the normal bridge-network way.

### Practical

```bash
docker run -d --name host-test --network host nginx
```

Check the container:

```bash
docker inspect host-test
```

We verified:

```text
NetworkMode: host
```

**Important points:**

- The container shares the host network.
- Port mapping such as `-p 8080:80` is not required or used in the usual way.
- The container uses the host's network ports directly.
- Port conflicts can occur if another application already uses the same port.

## 7. None Network

The none network completely isolates the container from external networking.

### Practical

```bash
docker run -dit --name none-test --network none ubuntu:24.04
```

Inspect the container:

```bash
docker inspect none-test
```

We observed:

- No external network connection.
- No normal container IP address or gateway.
- Internet access was unavailable.

**Use case:** Useful when a container should run without network communication.

## 8. Overlay Network

An overlay network allows containers running on different Docker hosts to communicate.

For example:

```text
EC2 Instance 1                 EC2 Instance 2

Container A  ----------------  Container B
                  |
             Overlay Network
```

Overlay networking is commonly associated with Docker Swarm.

**Learning note:** We covered overlay networking theoretically. We did not perform the practical because we were using only one EC2 instance. We plan to learn Kubernetes networking later.

## 9. Docker Network Commands

| Command | Purpose |
|---|---|
| `docker network ls` | List networks |
| `docker network create NAME` | Create a network |
| `docker network inspect NAME` | Inspect network details |
| `docker network rm NAME` | Remove a network |
| `docker network connect NAME CONTAINER` | Connect a container |
| `docker network disconnect NAME CONTAINER` | Disconnect a container |
| `docker network prune` | Remove unused networks |

**Note:** A network cannot be removed while containers are connected to it.

## 10. Network Cleanup

We removed our practice containers and custom networks.

Remove containers:

```bash
docker rm -f cone ctwo cthree host-test none-test
```

Remove custom networks:

```bash
docker network rm jionetwork secondnetwork
```

Check remaining networks:

```bash
docker network ls
```

Default networks normally remain:

```text
bridge
host
none
```

---

# Part 2: Docker Volumes

## 1. What is a Docker Volume?

A Docker volume is used to store data outside a container.

When we remove a container, data in its writable layer is removed. Docker volumes help preserve important data independently of the container.

**Simple example:**

Imagine MongoDB is running inside a container.

If we remove the container, database data in its writable layer is lost. If we store the database data in a Docker volume, we can use the same data with a new container.

## 2. Why Do We Need Docker Volumes?

- To store important application data.
- To protect data when containers are removed.
- To reuse the same data with another container.
- To separate application data from container lifecycle.

## 3. Types of Storage We Learned

1. Docker Volumes
2. Bind Mounts

### Docker Volume

Docker manages the storage location.

Example:

```bash
docker volume create mongodata
```

Docker stores local volume data under:

```text
/var/lib/docker/volumes/
```

### Bind Mount

We select a directory on the host machine and connect it to a directory inside the container.

Example:

```bash
-v /home/ubuntu/bind-data:/app/data
```

Here:

- `/home/ubuntu/bind-data` – EC2 host directory.
- `/app/data` – container directory.

## 4. Docker Volume Commands

### Create a Volume

```bash
docker volume create mongodata
```

Creates a named volume.

### List Volumes

```bash
docker volume ls
```

Displays all Docker volumes.

### Inspect a Volume

```bash
docker volume inspect mongodata
```

Displays volume details such as name, driver and mountpoint.

### Remove a Volume

```bash
docker volume rm mongodata
```

Removes a volume when it is not being used by a container.

### Remove Unused Anonymous Volumes

```bash
docker volume prune
```

Removes unused anonymous local volumes by default.

**Warning:** Always check before confirming because volume data can be permanently deleted.

## 5. MongoDB with Docker Volume

We used MongoDB to understand data persistence.

### Step 1: Create a Volume

```bash
docker volume create mongodata
```

### Step 2: Run MongoDB with the Volume

```bash
docker run -d --name mongo-db -v mongodata:/data/db mongo
```

Explanation:

- `-d` – runs in the background.
- `--name mongo-db` – container name.
- `-v` – connects a volume.
- `mongodata` – Docker volume name.
- `/data/db` – MongoDB data directory.
- `mongo` – MongoDB image.

### Step 3: Connect to MongoDB

```bash
docker exec -it mongo-db mongosh
```

### Step 4: Create a Database

```javascript
use devops
```

### Step 5: Insert Sample Data

```javascript
db.students.insertOne({name: "Ramana", course: "DevOps"})
```

### Step 6: View the Data

```javascript
db.students.find()
```

We successfully inserted and viewed the student record.

### Step 7: Exit MongoDB

```javascript
exit
```

### Step 8: Stop and Remove the Container

```bash
docker stop mongo-db
docker rm mongo-db
```

The container is removed, but the named volume remains.

### Step 9: Create a New Container Using the Same Volume

```bash
docker run -d --name mongo-db-new -v mongodata:/data/db mongo
```

### Step 10: Verify the Old Data

```bash
docker exec -it mongo-db-new mongosh
```

Inside MongoDB:

```javascript
use devops
db.students.find()
```

**Result:** We could still see the previously inserted student record.

This proves that Docker volumes preserve data even after removing the original container.

## 6. Anonymous Volumes

An anonymous volume is created without a user-provided volume name.

Docker images can define volume locations themselves.

For example, the MongoDB image also uses:

```text
/data/configdb
```

Even though we provided a named volume for `/data/db`, Docker automatically created an anonymous volume for `/data/configdb`.

List volumes:

```bash
docker volume ls
```

Inspect a volume:

```bash
docker volume inspect VOLUME_NAME
```

## 7. What is a Bind Mount?

A bind mount connects a directory or file from the EC2 host to a directory inside a container.

Example:

```text
EC2 Host                         Container

/home/ubuntu/bind-data   --->   /app/data
```

Both locations access the same files.

### Step 1: Create a Host Directory

```bash
mkdir bind-data
```

### Step 2: Create a File

```bash
echo "Hello from EC2 host" > bind-data/host.txt
```

### Step 3: Run an Ubuntu Container

```bash
docker run -dit --name bind-test \
-v /home/ubuntu/bind-data:/app/data ubuntu:24.04
```

### Step 4: Check the File Inside the Container

```bash
docker exec -it bind-test ls /app/data
```

Read the file:

```bash
docker exec -it bind-test cat /app/data/host.txt
```

Output:

```text
Hello from EC2 host
```

### Step 5: Create a File Inside the Container

```bash
docker exec -it bind-test sh -c 'echo "Hello from Docker container" > /app/data/container.txt'
```

### Step 6: Check the File on EC2

```bash
ls bind-data
```

Output:

```text
container.txt
host.txt
```

This proves that files are shared between the host and container.

## 8. Read-Only Bind Mount

Sometimes we want a container to read files but not modify them.

For this, we use `:ro`.

Example:

```bash
docker run -dit --name bind-readonly \
-v /home/ubuntu/bind-data:/app/data:ro ubuntu:24.04
```

Here, `:ro` means read-only.

Test writing:

```bash
docker exec -it bind-readonly sh -c 'echo "Testing read only" > /app/data/test.txt'
```

Expected result:

```text
Read-only file system
```

Read existing files:

```bash
docker exec -it bind-readonly ls /app/data
```

The container can read files but cannot write to the mounted directory.

## 9. MongoDB Using Bind Mount

We also practised running MongoDB using an EC2 host directory.

### Step 1: Create a Directory

```bash
mkdir mongo-bind-data
```

### Step 2: Run MongoDB

```bash
docker run -d --name mongo-bind \
-v /home/ubuntu/mongo-bind-data:/data/db mongo
```

### Step 3: Connect to MongoDB

```bash
docker exec -it mongo-bind mongosh
```

### Step 4: Create a Database

```javascript
use binddemo
```

### Step 5: Insert Sample Data

```javascript
db.students.insertOne({name: "Ramana", course: "Docker Bind Mount"})
```

### Step 6: Check MongoDB Files on EC2

Exit MongoDB:

```javascript
exit
```

Run:

```bash
ls -l mongo-bind-data
```

We could see MongoDB storage files such as:

- `WiredTiger.wt`
- `collection-*.wt`
- `index-*.wt`
- `storage.bson`

This confirms that MongoDB is storing its database files in the EC2 host directory.

### Step 7: Remove the Container

```bash
docker stop mongo-bind
docker rm mongo-bind
```

### Step 8: Check the Host Directory Again

```bash
ls mongo-bind-data
```

The MongoDB files are still present.

This proves that bind mount data remains on the host even after removing the container.

## 10. Docker Volume vs Bind Mount

| Docker Volume | Bind Mount |
|---|---|
| Managed by Docker | Managed by the user |
| Docker chooses the storage location | User chooses the host path |
| Stored under Docker's volume directory | Stored in the specified host directory |
| Easy to manage with Docker commands | Uses normal host files and directories |
| Example: `mongodata:/data/db` | Example: `/home/ubuntu/data:/data/db` |

## 11. Important Points to Remember

1. Removing a container does not automatically remove a named volume.
2. Docker volumes help preserve important application data.
3. Bind mounts connect host directories to container directories.
4. `:ro` makes a bind mount read-only.
5. A container can use both Docker volumes and bind mounts.
6. Docker images can automatically create anonymous volumes.
7. Stopping a container does not automatically delete its writable-layer data.
8. Removing a container removes its writable-layer data.
9. Always check before removing volumes because they may contain important data.

---

# Practical Learning Summary

## Docker Networking

- Understood Docker networking.
- Practised default bridge networking.
- Created a custom bridge network.
- Tested communication between containers using IP addresses.
- Tested container-name DNS resolution.
- Connected and disconnected containers from networks.
- Practised host networking.
- Practised none networking.
- Understood overlay networking theoretically.

## Docker Volumes

- Created and inspected Docker volumes.
- Listed and removed volumes.
- Learned about anonymous volumes.
- Used MongoDB with named volumes.
- Tested data persistence after container removal.
- Practised bind mounts.
- Shared files between EC2 and containers.
- Tested read-only bind mounts.
- Used MongoDB with bind mounts.
- Verified that database files remained on EC2 after removing the container.

## Conclusion

Docker Networking helps containers communicate with each other and external systems.

Docker Volumes and Bind Mounts help store application data safely.

These concepts are important when running real-world applications such as Java applications, MongoDB, MySQL and Jenkins.

## Next Topic

**Network Volumes**

We will learn how storage can be shared across multiple servers using:

- NFS (Network File System)
- AWS EBS (Elastic Block Store)

We will cover this topic later.