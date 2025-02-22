package com.raspberry.air.quality.service.impl;

import com.raspberry.air.quality.model.Sensor;
import com.raspberry.air.quality.model.SensorData;
import com.raspberry.air.quality.model.SensorType;
import com.raspberry.air.quality.service.SensorService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class HumiditySensorService implements SensorService {

  private final Random random = new Random();

  /**
   * Simulates obtaining the current humidity reading. In the future, this method can be replaced
   * with real sensor data retrieval.
   *
   * @return a SensorData object containing the humidity reading
   */
  public SensorData readSensors() {
    double humidity = generateRandomHumidity();
    return new SensorData(
        SensorType.HUMIDITY,
        LocalDateTime.now(),
        List.of(new Sensor("ID:001", humidity), new Sensor("ID:002", humidity)));
  }

  /** Generates a random humidity value within the range of 20% to 90%. */
  private double generateRandomHumidity() {
    return 20 + (70 * random.nextDouble());
  }
}
