package com.serjlemast.service.dth11;

import static com.serjlemast.reader.dht11.Dht11Gpio4SensorReader.HUMIDITY_ID;
import static com.serjlemast.reader.dht11.Dht11Gpio4SensorReader.TEMPERATURE_CELSIUS_ID;
import static com.serjlemast.reader.dht11.Dht11Gpio4SensorReader.TEMPERATURE_FAHRENHEIT_ID;

import com.serjlemast.model.sensor.Sensor;
import com.serjlemast.model.sensor.SensorData;
import com.serjlemast.model.sensor.SensorType;
import com.serjlemast.reader.dht11.Dht11Gpio4SensorReader;
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

  private static final String DEVICE_ID = "DHT11:004";

  private final Dht11Gpio4SensorReader dht11Gpio4SensorReader;

  @Override
  public Optional<Sensor> readSensorData() {
    return Optional.ofNullable(dht11Gpio4SensorReader.read())
        .filter(data -> !data.isEmpty())
        .map(data -> new Sensor(DEVICE_ID, SensorType.DHT_11, fetchSensorData(data)));
  }

  /**
   * Converts GPIO data into a list of sensor readings.
   *
   * @param data A map containing sensor IDs as keys and their corresponding values.
   * @return A list of {@link SensorData} objects with the retrieved sensor values.
   */
  public List<SensorData> fetchSensorData(Map<String, Number> data) {
    return Stream.of(TEMPERATURE_CELSIUS_ID, TEMPERATURE_FAHRENHEIT_ID, HUMIDITY_ID)
        .map(id -> new SensorData(id, data.get(id)))
        .toList();
  }
}
