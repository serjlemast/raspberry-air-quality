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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DHT11_Gpio4_SensorService implements SensorService {

  private final Gpio4Reader gpio4Reader;

  @Override
  public Optional<SensorData> readSensors() {
    var gpioDataOpt = gpio4Reader.read();
    if (gpioDataOpt.isEmpty()) {
      return Optional.empty();
    }

    Map<String, Number> gpioData = gpioDataOpt.get();

    return Optional.of(
        new SensorData(
            SensorType.ALL,
            List.of(
                new Sensor(TEMPERATURE_CELSIUS_ID, gpioData.get(TEMPERATURE_CELSIUS_ID)),
                new Sensor(TEMPERATURE_FAHRENHEIT_ID, gpioData.get(TEMPERATURE_FAHRENHEIT_ID)),
                new Sensor(HUMIDITY_ID, gpioData.get(HUMIDITY_ID)))));
  }
}
