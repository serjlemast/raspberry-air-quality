package com.serjlemast.scheduler;

import com.serjlemast.publisher.RabbitMqPublisher;
import com.serjlemast.service.SensorService;
import java.util.List;
import java.util.Map;
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
  private final DHT11Service dht11Service;

  @Scheduled(cron = "*/10 * * * * *")
  public void process() {

    Map<String, Object> temperatureAndHumidity = dht11Service.getTemperatureAndHumidity();

    log.info("Processing temperature and humidity - {}", temperatureAndHumidity);

    //    wrapper(
    //        () -> {
    //          var sensors = sensorServices.stream().map(SensorService::readSensors).toList();
    //          var event = new SensorDataEvent(timestamp, sensors);
    //          publisher.publish(event);
    //        });
  }

  private void wrapper(Runnable r) {
    try {
      r.run();
    } catch (Exception e) {
      log.error("Error writing sensor data", e);
    }
  }
}
