package com.serjlemast.reader.ccs811;

import com.serjlemast.reader.SensorReader;
import java.io.*;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Ccs811I2CSensorReader extends SensorReader {

  private static final String SCRIPT_NAME = "ccs811_i2c_reader.py";

  @Override
  public Map<String, Number> read() {
    try {
      // If mock mode is enabled, return randomly generated sensor data.
      // Otherwise, return an empty Optional (indicating real sensor data should be used).
      if (mockEnable) {
        return generateMockData();
      }

      var tempScriptFile = extractPythonScript(SCRIPT_NAME, "ccs811_i2c_reader", ".py");

      return readScript(tempScriptFile);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Collections.emptyMap();
    }
  }

  @Override
  protected Map<String, Number> generateMockData() {
    int tvoc = ThreadLocalRandom.current().nextInt(0, 600);
    int eco2 = ThreadLocalRandom.current().nextInt(400, 2000);

    return Map.of(TOTAL_VOLATILE_ORGANIC_COMPOUND_ID, tvoc, CARBON_DIOXIDE_ID, eco2);
  }
}
