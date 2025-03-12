package com.serjlemast.scheduler;

import com.serjlemast.model.SensorDataEvent;
import com.serjlemast.publisher.RabbitMqPublisher;
import com.serjlemast.service.SensorService;
import java.time.LocalDateTime;
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
  private final List<SensorService> sensorServices;

  @Scheduled(cron = "*/10 * * * * *")
  public void process() {
    wrapper(
        () -> {
          var sensorDataList =
              sensorServices.stream()
                  .map(SensorService::readSensors)
                  .filter(Optional::isPresent)
                  .map(Optional::get)
                  .toList();

          if (sensorDataList.isEmpty()) {
            log.info("No sensors found");
            return;
          }

          var event = new SensorDataEvent(LocalDateTime.now(), sensorDataList);
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
