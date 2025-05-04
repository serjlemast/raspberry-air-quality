### Raspberry Pi Air Quality app

#### Project setup

* Java 23
* Springboot 3.4.3
* Gradle 8.13

#### Project configuration

Add rabbitMQ properties to `.env` file:

```
APP_SERVICE_RABBITMQ_HOST=cow.rmq2.cloudamqp.com
APP_SERVICE_RABBITMQ_PORT=5672
APP_SERVICE_RABBITMQ_USERNAME=......
APP_SERVICE_RABBITMQ_PASSWORD=........
APP_SERVICE_RABBITMQ_VIRTUAL_HOST=......

APP_SERVICE_RABBITMQ_QUEUE=air-quality-queue (by default)
APP_SERVICE_RABBITMQ_QUEUE_TTL=6000 (6 sec by defauult)
APP_SERVICE_RABBITMQ_EXCHANGE=air-quality-exchange (by default)
APP_SERVICE_RABBITMQ_ROUTING_KEY=air-quality-routing-key (by default)

APP_SERVICE_CRONE=*/1 * * * * * (run scheduler every 1 sec by default)
```

Note: for CloudAMQP RabbitMQ provider `RABBITMQ_VIRTUAL_HOST` is `RABBITMQ_USERNAME`

#### Provider

* https://www.cloudamqp.com (Message Queues in the Cloud)

##### CloudAMQP RabbitMQ provider

link: https://api.cloudamqp.com

plan: 'Little Lemur'

* Open Connections: 0 of 20
* Max Idle Queue Time: 28 days
* Queues: 2 of 150
* Messages: 7 of 1 000 000
* Queue Length: 1 of 10 000

##### Switching to CloudAMQP

I had the same error when I switched to Cloud AMQP.

link: https://stackoverflow.com/questions/72248024/switching-to-cloudamqp-gives-com-rabbitmq-client-shutdownsignalexception

As you mentioned, the virtual host was missing from the properties:

```
spring.rabbitmq.virtual-host=user_name

spring.rabbitmq.host=.....
spring.rabbitmq.username=user_name
spring.rabbitmq.password=........
spring.rabbitmq.port=5672
```

### Remote JVM debug

1. Add configuration IDE

Edit configurations >  Run / Debug > Remote > Remote Debugging or Debugging

Debug mode: `Attach to remote JVM`

HOST: `your.raspberry.pi.ip`
PORT: `5005`

###### use command 'ifconfig' on raspberry for ipaddress

Command line arguments for remote JVM: `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005` 
User module classpath: `raspberry-air-quality.main`

2. Run java app on raspberry:

```
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar build/libs/spring-boot-application.jar
```

or

```
java -Xmx256m -Xms128m -XX:+UseG1GC -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar build/libs/spring-boot-application.jar
```

### Minimum memory settings

If your application is very lightweight, you can try:

```
java -Xmx128m -Xms64m -XX:+UseSerialGC -jar build/libs/spring-boot-application.jar
```

However, if you use a database, REST API, or caching, 128MB might not be enough.
In that case, it's safer to allocate at least 256MB:

```
java -Xmx256m -Xms128m -XX:+UseG1GC -jar build/libs/spring-boot-application.jar
```

### Raspberry pi 4

#### Raspberry Pi: Measure Humidity and Temperature with DHT11/DHT22

link: https://tutorials-raspberrypi.com/raspberry-pi-measure-humidity-temperature-dht11-dht22/

#### Raspberry Pi Humidity Software Installation

First of all, some packages have to be installed:

```
sudo apt-get update
sudo apt-get install build-essential python-dev python-openssl git
```

The library for the sensors can now be loaded. I use a ready-made library from Adafruit that supports various sensors

```
sudo pip3 install adafruit-circuitpython-dht
sudo apt-get install libgpiod2
```

#### Raspberry Pi: Measure CO2 and TVOC with CCS811

link: [Adafruit CircuitPython CCS811 Library](https://github.com/adafruit/Adafruit_CircuitPython_CCS811/tree/main)

#### Raspberry Pi CO2 Software Installation

First of all, some packages have to be installed:

```
sudo apt-get update
sudo apt-get install build-essential python-dev python-openssl git
```

The library for the sensor can now be installed from PyPI:

```
sudo pip3 install adafruit-circuitpython-ccs811
```

To install system-wide (may be required in some cases):

```
sudo pip3 install adafruit-circuitpython-ccs811
```

To install in a virtual environment in your current project:

```
mkdir project-name && cd project-name
python3 -m venv .venv
source .venv/bin/activate
pip3 install adafruit-circuitpython-ccs811
```

#### Creating a Systemd Service

Run the following command to create a new service file:

```
sudo nano /etc/systemd/system/air-quality.service
```

Copy and paste this content into the file:

```
[Unit]
Description=Raspberry Pi Air Quality Monitoring Application
After=network.target

[Service]
User=pi
WorkingDirectory=/home/pi/<projectPath>
ExecStart=/usr/bin/java -Xmx256m -Xms128m -XX:+UseG1GC -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar spring-boot-application.jar
SuccessExitStatus=143
Restart=always
RestartSec=5
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```
Reload Systemd and Enable the Service

```
sudo systemctl daemon-reload
sudo systemctl enable <serviceName>.service
```

Start the Service

```
sudo systemctl start <serviceName>.service
```

Check Service Status

```
sudo systemctl status <serviceName>.service
```

Logs

```
journalctl -u <serviceName>.service -n 50 --no-pager
```

#### Java code style

Java code style refers to the conventions and guidelines that developers follow when writing Java code to ensure
consistency and readability.

project: google-java-format,
link: https://github.com/google/google-java-format/blob/master/README.md#intellij-jre-config

#### Gradle

##### Gradle Versions Plugin

Displays a report of the project dependencies that are up-to-date, exceed the latest version found, have upgrades, or
failed to be resolved, info: https://github.com/ben-manes/gradle-versions-plugin

command:

```
gradle dependencyUpdates
```

##### Gradle wrapper

Gradle Wrapper Reference:
https://docs.gradle.org/current/userguide/gradle_wrapper.html

How to Upgrade Gradle Wrapper:
https://dev.to/pfilaretov42/tiny-how-to-upgrade-gradle-wrapper-3obl

```
./gradlew wrapper --gradle-version latest
```

##### Gradle ignore test

To skip any task from the Gradle build, we can use the -x or –exclude-task option. In this case, we’ll use “-x test” to
skip tests from the build.

To see it in action, let’s run the build command with -x option:

```
gradle clean build -x test
```