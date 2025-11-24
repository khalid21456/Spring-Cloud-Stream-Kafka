# Spring Cloud Stream Kafka

This project demonstrates a basic Spring Cloud Stream application using Kafka as the message broker. It includes a REST controller to publish events to a Kafka topic and a service to consume those events.

## Overview

The application consists of the following components:

-   **KafkaApplication:** The main Spring Boot application class.
-   **PageEvent:** A simple entity representing a page event with attributes like name, user, date, and duration.
-   **PageEventService:** A service that provides a Kafka consumer to process `PageEvent` messages from a Kafka topic.
-   **PageEventRestController:** A REST controller that exposes an endpoint to publish `PageEvent` messages to a Kafka topic using Spring Cloud Stream's `StreamBridge`.


## 📁 Project Structure
```
khalid21456-spring-cloud-stream-kafka/
├── mvnw
├── mvnw.cmd
├── pom.xml
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/example/kafka/
    │   │       ├── KafkaApplication.java           # Main application entry point
    │   │       ├── entities/
    │   │       │   └── PageEvent.java              # Event entity/model
    │   │       ├── services/
    │   │       │   └── PageEventService.java       # Kafka functional beans
    │   │       └── web/
    │   │           └── PageEventRestController.java # REST API controller
    │   └── resources/
    │       └── application.properties              # Kafka & Stream configuration
    └── test/
        └── java/
            └── com/example/kafka/
                └── KafkaApplicationTests.java      # Unit tests
```

### 📄 Key Files

| File | Description |
|------|-------------|
| `KafkaApplication.java` | Spring Boot main application class |
| `PageEvent.java` | Event model representing page view data |
| `PageEventService.java` | Contains Producer, Consumer, and Processor beans |
| `PageEventRestController.java` | REST endpoints for publishing events |
| `application.properties` | Kafka topics, bindings, and Stream configuration |


## Components

## 1. PageEvent.java

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


## 2. PageEventRestService.java


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

This REST controller exposes an endpoint /publish/{topic}/{name} to publish PageEvent messages to a specified Kafka topic. (PageEventRestController.java) It uses StreamBridge to send messages to Kafka. The publish method creates a new PageEvent with random data and sends it to the topic specified in the path.
## 3. PageEventService.java

```java
package com.example.kafka.services;

import com.example.kafka.entities.PageEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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

    @Bean
    public Supplier<PageEvent> pageEventSupplier() {
        return () -> new PageEvent(
                Math.random() > 0.5?"P1":"P2",
                Math.random() > 0.5?"U1":"U2",
                new Date(),new Random().nextInt(9000));
    }

    @Bean
    public Function<PageEvent,PageEvent> pageEventFunction() {
        return (input) -> {
            input.setName("Page Event");
            input.setUser("UUUUUUUUU");
            return input;
        };
    }

}

```
### 📝 Service Implementation

The `PageEventService` class demonstrates three core Kafka messaging patterns using Spring Cloud Stream's functional programming model:

- **Producer (`pageEventSupplier`)**: Automatically generates random `PageEvent` messages at regular intervals, simulating user page visits with randomized page IDs (P1/P2) and user IDs (U1/U2), along with timestamps and visit durations.

- **Consumer (`pageEventConsumer`)**: Listens to incoming `PageEvent` messages from Kafka topics and processes them by printing the event details to the console, demonstrating basic message consumption.

- **Processor (`pageEventFunction`)**: Acts as a stream processor that transforms messages in real-time by modifying the incoming `PageEvent` data (setting name to "Page Event" and user to "UUUUUUUUU") before forwarding it to the next topic in the pipeline.

These three functional beans (`Supplier`, `Consumer`, `Function`) are automatically bound to Kafka topics by Spring Cloud Stream, enabling event-driven communication without explicit Kafka API calls.

## 4. application.properties
```properties
spring.cloud.function.definition=pageEventConsumer;pageEventSupplier;pageEventFunction

spring.cloud.stream.function.routing.enabled=true

spring.cloud.stream.bindings.pageEventConsumer-in-0.destination=R1
spring.cloud.stream.bindings.pageEventConsumer-in-0.group=group1

spring.cloud.stream.bindings.pageEventSupplier-out-0.destination=R2
spring.cloud.stream.bindings.pageEventSupplier-out-0.group=group2
spring.cloud.stream.poller.fixed-delay=100

spring.cloud.stream.bindings.pageEventFunction-in-0.destination=R1
spring.cloud.stream.bindings.pageEventFunction-out-0.destination=R3
spring.cloud.stream.bindings.pageEventFunction-in-0.group=group1
spring.cloud.stream.bindings.pageEventFunction-out-0.group=group3
```

Check my blog post : https://dev.to/khalid_edaoudi_f60d2bbc68/building-a-spring-boot-application-with-spring-cloud-stream-for-kafka-stream-processing-3h6a
