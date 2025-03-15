package com.serjlemast.scheduler;

import com.serjlemast.publisher.RabbitMqPublisher;
import com.serjlemast.service.SensorService;
import com.serjlemast.service.raspberry.RaspberryService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerProcessor {

  private final RabbitMqPublisher publisher;
  private final RaspberryService raspberryService;
  private final List<SensorService> sensorServices;

  @Scheduled(cron = "${scheduled.cron}")
  public void process() {
    wrapper(
        () -> {
          var info = raspberryService.getInfo();
          var sensors =
              sensorServices.stream()
                  .map(SensorService::readSensorData)
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .toList();

          if (sensors.isEmpty()) {
            log.warn("No sensors detected for the Raspberry Pi controller: {}", info);
            return;
          }

          publisher.publish(info, sensors);
        });
  }

  private void wrapper(Runnable r) {
    try {
      r.run();
    } catch (Exception ex) {
      log.error("Failed to publish data from the Raspberry Pi controller", ex);
    }
  }
}
