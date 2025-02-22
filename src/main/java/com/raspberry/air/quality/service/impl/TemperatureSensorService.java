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
public class TemperatureSensorService implements SensorService {

  private final Random random = new Random();

  /**
   * Simulates obtaining the current temperature reading. In the future, this method can be replaced
   * with real sensor data retrieval.
   *
   * @return a SensorData object containing the temperature reading
   */
  public SensorData readSensors() {
    double temperature = generateRandomTemperature();
    return new SensorData(
        SensorType.HUMIDITY,
        LocalDateTime.now(),
        List.of(new Sensor("ID:004", temperature), new Sensor("ID:005", temperature)));
  }

  /** Generates a random temperature within the range of -10 to 35 degrees Celsius. */
  private double generateRandomTemperature() {
    return -10 + (45 * random.nextDouble());
  }
}
