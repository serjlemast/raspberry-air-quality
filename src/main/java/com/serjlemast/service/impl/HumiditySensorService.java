package com.serjlemast.service.impl;

import com.serjlemast.model.Sensor;
import com.serjlemast.model.SensorData;
import com.serjlemast.model.SensorType;
import com.serjlemast.service.SensorService;
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
    var sensorId001 = new Sensor("ID:001", generateRandomHumidity());
    var sensorId002 = new Sensor("ID:002", generateRandomHumidity());
    var sensorId003 = new Sensor("ID:003", generateRandomHumidity());

    return new SensorData(SensorType.HUMIDITY, List.of(sensorId001, sensorId002, sensorId003));
  }

  /** Generates a random humidity value within the range of 20% to 90%. */
  private double generateRandomHumidity() {
    return 20 + (70 * random.nextDouble());
  }
}
