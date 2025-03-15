package com.serjlemast.service.dth11;

import static com.serjlemast.gpio.dht11.Dht11Gpio4SensorReader.HUMIDITY_ID;
import static com.serjlemast.gpio.dht11.Dht11Gpio4SensorReader.TEMPERATURE_CELSIUS_ID;
import static com.serjlemast.gpio.dht11.Dht11Gpio4SensorReader.TEMPERATURE_FAHRENHEIT_ID;

import com.serjlemast.gpio.dht11.Dht11Gpio4SensorReader;
import com.serjlemast.model.sensor.Sensor;
import com.serjlemast.model.sensor.SensorData;
import com.serjlemast.model.sensor.SensorType;
import com.serjlemast.service.SensorService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Dht11Gpio4SensorService implements SensorService {

  private final Dht11Gpio4SensorReader dht11Gpio4SensorReader;

  @Override
  public Optional<Sensor> readSensorData() {
    return dht11Gpio4SensorReader
        .read()
        .map(gpioData -> new Sensor(SensorType.DHT_11, fetchDh11SensorData(gpioData)));
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
