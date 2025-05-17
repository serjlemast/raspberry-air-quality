package com.serjlemast.scheduler;

import com.serjlemast.publisher.RabbitMqPublisher;
import com.serjlemast.service.collector.SensorDataCollectorService;
import com.serjlemast.service.raspberry.RaspberryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerProcessor {

  private final RabbitMqPublisher publisher;
  private final RaspberryService raspberryService;

  private final SensorDataCollectorService sensorDataCollectorService;

  @Async
  @Scheduled(cron = "${scheduled.cron}")
  public void collectSensorDataJob() {
    try {
      // 1 collect all info about sensors
      sensorDataCollectorService
          .findAllInformationAboutSensors()
          .ifPresent(
              sensors -> {
                // 2 collect data from raspberry
                var info = raspberryService.getInfo();
                // 3 send info
                publisher.publish(info, sensors);
              });

    } catch (Exception ex) {
      log.error("Failed to publish data from the Raspberry Pi controller", ex);
    }
  }
}
