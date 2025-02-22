### Raspberry Pi Air Quality app

#### Project setup

* Java 23
* Springboot 3.4.2
* Gradle 8.12

#### Project configuration

Add rabbitMQ properties to `.env` file:

```
RABBITMQ_HOST=cow.rmq2.cloudamqp.com
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=......
RABBITMQ_PASSWORD=........
RABBITMQ_VIRTUAL_HOST=......
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