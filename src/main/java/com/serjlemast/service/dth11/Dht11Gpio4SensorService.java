package com.serjlemast.service.dth11;

import static com.serjlemast.gpio.dht11.Dht11Gpio4SensorReader.HUMIDITY_ID;
import static com.serjlemast.gpio.dht11.Dht11Gpio4SensorReader.TEMPERATURE_CELSIUS_ID;
import static com.serjlemast.gpio.dht11.Dht11Gpio4SensorReader.TEMPERATURE_FAHRENHEIT_ID;

import com.serjlemast.gpio.dht11.Dht11Gpio4SensorReader;
import com.serjlemast.model.Sensor;
import com.serjlemast.model.SensorData;
import com.serjlemast.model.SensorType;
import com.serjlemast.service.SensorService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Dht11Gpio4SensorService implements SensorService {

  private final Dht11Gpio4SensorReader dht11Gpio4SensorReader;

  @Override
  public Optional<Sensor> readSensor() {
    return dht11Gpio4SensorReader
        .read()
        .map(gpioData -> new Sensor(SensorType.ALL, getSensorData(gpioData)));
  }

  private List<SensorData> getSensorData(Map<String, Number> gpioData) {
    return Stream.of(TEMPERATURE_CELSIUS_ID, TEMPERATURE_FAHRENHEIT_ID, HUMIDITY_ID)
        .map(id -> new SensorData(id, gpioData.get(id)))
        .toList();
  }
}
