package com.raspberry.air.quality;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RunApplication {

  public static void main(String[] args) {
    SpringApplication.run(RunApplication.class, args);
  }
}
