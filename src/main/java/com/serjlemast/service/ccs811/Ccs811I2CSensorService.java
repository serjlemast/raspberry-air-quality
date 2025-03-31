package com.serjlemast.service.ccs811;

import static com.serjlemast.reader.ccs811.Ccs811I2CSensorReader.*;

import com.serjlemast.reader.ccs811.Ccs811I2CSensorReader;
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
public class Ccs811I2CSensorService implements SensorService {
  private static final String DEVICE_ID = "CCS811:001";

  private final Ccs811I2CSensorReader ccs811I2CSensorReader;

  @Override
  public Optional<Sensor> readSensorData() {
    return ccs811I2CSensorReader
        .read()
        .map(i2cData -> new Sensor(DEVICE_ID, SensorType.CCS811, fetchCcs811SensorData(i2cData)));
  }

  /**
   * Converts GPIO data into a list of sensor readings.
   *
   * @param i2cData A map containing sensor IDs as keys and their corresponding values.
   * @return A list of {@link SensorData} objects with the retrieved sensor values.
   */
  private List<SensorData> fetchCcs811SensorData(Map<String, Number> i2cData) {
    return Stream.of(TOTAL_VOLATILE_ORGANIC_COMPOUND_ID, CARBON_DIOXIDE_ID)
        .map(id -> new SensorData(id, i2cData.get(id)))
        .toList();
  }
}
