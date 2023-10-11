[![](https://github.com/d1snin/delrey/actions/workflows/build.yml/badge.svg)](https://github.com/d1snin/delrey/actions/workflows/build.yml)

# Delrey

Delrey is a very small remote shell access toolkit. It uses JDK's standard `ProcessBuilder` API to spawn processes via
commands received from
the Master server through WebSocket connection.

It is not really stable and is not intended to be used in production environments. I made it because I needed simple and
lightweight solution to
control (well, try to) my devices.

## Usage

You have to boot up your own Delrey Master server which the clients will connect to.
Then you can run client binaries on any host you would like to control remotely.

Clone the repo:

```shell
git clone https://github.com/d1snin/delrey
cd delrey
```

### Configuring Delrey Master server

Copy environment configuration:

```shell
cp ./delrey-master/.env.tmp ./delrey-master/.env
```

Now provide your own configuration in `./delrey-master/.env`.
I advise you modifying only the server token property: `DELREY_MASTER_SECURITY_TOKEN`
It is used for authorization process.

The preferred way to run Delrey Master is using Docker + Docker Compose

Build Docker image:

```shell
./gradlew delrey-master:publishImageToLocalRegistry
```

Run Docker container:

```shell
docker-compose -f ./delrey-master/docker/docker-compose.yml up -d
```

### Configuring Delrey Daemon

Build Daemon distribution (you can also download it as [Actions artifact](https://github.com/d1snin/delrey/actions))

```
./gradlew delrey-daemon:shadowJar
```

Run distribution (minimum JRE version is 11):

```shell
java -jar masterHttpBase=... masterWsBase=... whoami=...
```

Where:
`masterHttpBase`: Base HTTP URL of Delrey Master server, e.g. `https://rc.d1s.dev`\
`masterWsBase`: Base WebSocket URL of Delrey Master server, e.g. `wss://rc.d1s.dev`\
`whoami`: Current host identifier. Any value, e.g. `my-laptop`

*Note:* provided configuration will be automatically stored in current working directory, so there is no
need to repeat the same configuration each run.

I suggest configuring your system to run Daemon distribution on startup.

### Sending requests

**Fetch current Master server status via `GET /status`:**

```http request
GET https://rc.example.com/status
```
```http request
HTTP/1.1 200 OK
Content-Length: 49
Content-Type: application/json
Connection: keep-alive

{
    "version": "0.0.1",
    "state": "UP",
    "hosts": [
        "test"
    ]
}
```

**Post command to host via `POST /runs`**

```http request
POST https://rc.example.com/runs
Content-Type: application/json
Authorization: <token>

{
    "command": "<command (e.g. pwd)>",
    "host": "<host name (whoami)>"
}
```
```http request
HTTP/1.1 202 Accepted
Content-Length: 151
Content-Type: application/json
Connection: keep-alive

{
    "id": "a34b4887-ec04-434a-b3ff-af426be4ec25",
    "command": "pwd",
    "host": "test",
    "pid": null,
    "output": null,
    "status": null,
    "error": null
    "finished": false
}
```

You can also wait for run completion by specifying `wait` query parameter set to `true`:

```http request
POST https://rc.example.com/runs?wait=true
Content-Type: application/json
Authorization: <token>

{
    "command": "<command (e.g. pwd)>",
    "host": "<host name (whoami)>"
}
```
```http request
HTTP/1.1 200 OK
Content-Length: 191
Content-Type: application/json
Connection: keep-alive

{
    "id": "d506d2dd-c566-4812-aae9-64fc1b01c392",
    "command": "pwd",
    "host": "test",
    "pid": 34063,
    "output": "/home/d1snin/projects/delrey",
    "status": 0,
    "error": null,
    "finished": true
}
```

**Get run state via `GET /runs/{id}`**

```http request
GET https://rc.example.com/runs/<run id>
Content-Type: application/json
Authorization: <token>
```
```http request
HTTP/1.1 200 OK
Content-Length: 148
Content-Type: application/json
Connection: keep-alive

{
    "id": "a34b4887-ec04-434a-b3ff-af426be4ec25",
    "command": "pwd",
    "host": "test",
    "pid": 34060,
    "output": "/home/d1snin/projects/delrey",
    "status": null,
    "error": null,
    "finished": true
}
```

**Get runs by host via `GET /runs?host={host alias}`**

```http request
GET https://rc.example.com/runs?host=<host alias>
Authorization: <token>
```
```http request
HTTP/1.1 200 OK
Content-Length: 193
Content-Type: application/json
Connection: keep-alive

[
    {
        "id": "3ed2bcde-207d-4970-8f8e-3b39d6be9626",
        "command": "pwd",
        "host": "test",
        "pid": 56675,
        "output": "/home/d1snin/projects/delrey",
        "status": 0,
        "error": null,
        "finished": true
    }
]
```

## Code of Conduct

Please refer to [CODE_OF_CONDUCT.md](./CODE_OF_CONDUCT.md).

## License

```
Copyright 2023 Mikhail Titov <me@d1s.dev>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```