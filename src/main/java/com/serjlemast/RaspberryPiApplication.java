package com.serjlemast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
* todo:
*   1. JDK 24
*   2. Gradle 8.14
*   3. Spring Boot 3.4.5
 */
@SpringBootApplication
public class RaspberryPiApplication {

  public static void main(String[] args) {
    SpringApplication.run(RaspberryPiApplication.class, args);
  }
}
