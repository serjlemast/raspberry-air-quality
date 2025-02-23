package com.serjlemast.service.impl;

import com.serjlemast.model.Sensor;
import com.serjlemast.model.SensorData;
import com.serjlemast.model.SensorType;
import com.serjlemast.service.SensorService;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class TemperatureSensorService implements SensorService {

  private final Random random = new Random();

  /**
   * Simulates obtaining the current data reading. In the future, this method can be replaced with
   * real sensor data retrieval.
   *
   * @return a SensorData object containing the data reading
   */
  public SensorData readSensors() {
    var sensorId001 = new Sensor("ID:001", generateRandomTemperature());
    var sensorId002 = new Sensor("ID:002", generateRandomTemperature());

    return new SensorData(SensorType.TEMPERATURE, List.of(sensorId001, sensorId002));
  }

  /** Generates a random data within the range of -10 to 35 degrees Celsius. */
  private double generateRandomTemperature() {
    return -10 + (45 * random.nextDouble());
  }
}
