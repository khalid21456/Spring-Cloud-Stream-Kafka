# Spring Cloud Stream Kafka

This project demonstrates a basic Spring Cloud Stream application using Kafka as the message broker. It includes a REST controller to publish events to a Kafka topic and a service to consume those events.

## Overview

The application consists of the following components:

-   **KafkaApplication:** The main Spring Boot application class.
-   **PageEvent:** A simple entity representing a page event with attributes like name, user, date, and duration.
-   **PageEventService:** A service that provides a Kafka consumer to process `PageEvent` messages from a Kafka topic.
-   **PageEventRestController:** A REST controller that exposes an endpoint to publish `PageEvent` messages to a Kafka topic using Spring Cloud Stream's `StreamBridge`.

## Code Structure

The project structure is organized as follows:

khalid21456-spring-cloud-stream-kafka/
├── mvnw
├── mvnw.cmd
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/kafka/
│   │   │       ├── KafkaApplication.java
│   │   │       ├── entities/
│   │   │       │   └── PageEvent.java
│   │   │       ├── services/
│   │   │       │   └── PageEventService.java
│   │   │       └── web/
│   │   │           └── PageEventRestController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/example/kafka/
│               └── KafkaApplicationTests.java
└── .mvn/
└── wrapper/
└── maven-wrapper.properties


## Components

### 1. PageEvent.java

```java
package com.example.kafka.entities;

import lombok.*;

import java.util.Date;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
public class PageEvent {
private String name;
private String user;
private Date date;
private long duration;
}
```
This class defines the structure of a page event. (PageEvent.java)
It uses Lombok annotations (@Getter, @Setter, @AllArgsConstructor, @NoArgsConstructor, @ToString) to automatically generate boilerplate code.


## 2. PageEventService.java


```java
package com.example.kafka.services;

import com.example.kafka.entities.PageEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class PageEventService {

    @Bean
    public Consumer<PageEvent> pageEventConsumer() {
        return (input) -> {
            System.out.println("**************************************");
            System.out.println(input.toString());
            System.out.println("**************************************");
        };
    }

}

```

This service defines a Kafka consumer using a @Bean of type Consumer<PageEvent>. (PageEventService.java)
The pageEventConsumer method returns a Consumer that simply prints the received PageEvent to the console.
This consumer is configured in application.properties to listen to a specific Kafka topic.

## 3. PageEventRestController.java

```java
package com.example.kafka.web;


import com.example.kafka.entities.PageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Random;

@RestController
public class PageEventRestController {

    @Autowired
    private StreamBridge streamBridge;

    @GetMapping("/publish/{topic}/{name}")
    public PageEvent publish(@PathVariable String topic, @PathVariable String name) {
        PageEvent pageEvent = new PageEvent(name,Math.random() > 0.5?"U1":"U2",new Date(),new Random().nextInt(9000));
        streamBridge.send(topic,
                pageEvent);
        return pageEvent;
    }

}

```
This REST controller exposes an endpoint /publish/{topic}/{name} to publish PageEvent messages to a specified Kafka topic. (PageEventRestController.java)
It uses StreamBridge to send messages to Kafka.
The publish method creates a new PageEvent with random data and sends it to the topic specified in the path.

## 4. application.properties
```properties
spring.cloud.function.definition=pageEventConsumer
spring.cloud.stream.bindings.pageEventConsumer-in-0.destination=R1
spring.cloud.stream.bindings.pageEventConsumer-in-0.group=group1
```

