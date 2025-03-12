package com.serjlemast.service.impl;

import static com.serjlemast.gpio.Gpio4Reader.HUMIDITY_ID;
import static com.serjlemast.gpio.Gpio4Reader.TEMPERATURE_CELSIUS_ID;
import static com.serjlemast.gpio.Gpio4Reader.TEMPERATURE_FAHRENHEIT_ID;

import com.serjlemast.gpio.Gpio4Reader;
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
public class DHT11Gpio4SensorService implements SensorService {

  private final Gpio4Reader gpio4Reader;

  @Override
  public Optional<Sensor> readSensor() {
    return gpio4Reader.read().map(gpioData -> new Sensor(SensorType.ALL, getSensorData(gpioData)));
  }

  private List<SensorData> getSensorData(Map<String, Number> gpioData) {
    return Stream.of(TEMPERATURE_CELSIUS_ID, TEMPERATURE_FAHRENHEIT_ID, HUMIDITY_ID)
        .map(id -> new SensorData(id, gpioData.get(id)))
        .toList();
  }
}
