package com.serjlemast.service.dth11;

import static com.serjlemast.reader.dht11.Dht11Gpio17SensorReader.*;

import com.serjlemast.model.sensor.Sensor;
import com.serjlemast.model.sensor.SensorData;
import com.serjlemast.model.sensor.SensorType;
import com.serjlemast.reader.dht11.Dht11Gpio17SensorReader;
import com.serjlemast.service.SensorService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Dht11Gpio17SensorService implements SensorService {

  private static final String DEVICE_ID = "DHT11:017";

  private final Dht11Gpio17SensorReader dht11Gpio4SensorReader;

  @Override
  public Optional<Sensor> readSensorData() {
    return dht11Gpio4SensorReader
        .read()
        .map(gpioData -> new Sensor(DEVICE_ID, SensorType.DHT_11, fetchDh11SensorData(gpioData)));
  }

  /**
   * Converts GPIO data into a list of sensor readings.
   *
   * @param gpioData A map containing sensor IDs as keys and their corresponding values.
   * @return A list of {@link SensorData} objects with the retrieved sensor values.
   */
  private List<SensorData> fetchDh11SensorData(Map<String, Number> gpioData) {
    return Stream.of(TEMPERATURE_CELSIUS_ID, TEMPERATURE_FAHRENHEIT_ID, HUMIDITY_ID)
        .map(id -> new SensorData(id, gpioData.get(id)))
        .toList();
  }
}
