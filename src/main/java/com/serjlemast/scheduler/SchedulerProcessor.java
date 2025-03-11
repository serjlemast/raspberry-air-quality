package com.serjlemast.scheduler;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.platform.Platforms;
import com.serjlemast.model.SensorDataEvent;
import com.serjlemast.publisher.RabbitMqPublisher;
import com.serjlemast.service.SensorService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerProcessor {

  private final RabbitMqPublisher publisher;
  private final List<SensorService> sensorServices;

  private final Context pi4j = Pi4J.newAutoContext();

  @Scheduled(cron = "${scheduled.cron}")
  public void process() {

    Platforms platforms = pi4j.platforms();

    int PIN_4 = 4;

    var out = pi4j.digitalOutput().create(PIN_4);

    var timestamp = LocalDateTime.now();
    var threadName = Thread.currentThread().getName();
    log.info("Starting sensor data processing at {} on thread: {}", timestamp, threadName);

    wrapper(
        () -> {
          var sensors = sensorServices.stream().map(SensorService::readSensors).toList();
          var event = new SensorDataEvent(timestamp, sensors);
          publisher.publish(event);
        });
  }

  private void wrapper(Runnable r) {
    try {
      r.run();
    } catch (Exception e) {
      log.error("Error writing sensor data", e);
    }
  }
}
