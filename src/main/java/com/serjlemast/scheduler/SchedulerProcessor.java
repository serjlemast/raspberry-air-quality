package com.serjlemast.scheduler;

import com.serjlemast.publisher.RabbitMqPublisher;
import com.serjlemast.service.SensorService;
import java.time.LocalTime;
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

  @Scheduled(cron = "${scheduled.cron}")
  public void process() {
    var timestamp = LocalTime.now();
    var threadName = Thread.currentThread().getName();
    log.info("Starting sensor data processing at {} on thread: {}", timestamp, threadName);

    sensorServices.forEach(
        service ->
            wrapper(
                () -> {
                  var data = service.readSensors();
                  log.debug("Publishing data: {}", data);
                  publisher.publish(data);
                }));
  }

  private void wrapper(Runnable r) {
    try {
      r.run();
    } catch (Exception e) {
      log.error("Error writing sensor data", e);
    }
  }
}
