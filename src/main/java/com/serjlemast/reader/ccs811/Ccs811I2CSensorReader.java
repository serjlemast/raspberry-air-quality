package com.serjlemast.reader.ccs811;

import com.serjlemast.reader.SensorReaderTest;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Ccs811I2CSensorReader extends SensorReaderTest {

  private static final String SCRIPT_NAME = "ccs811_i2c_reader.py";

  @Override
  public Map<String, Number> read() {
    if (mockEnable) {
      return generateMockData();
    }

    try {
      File scriptFile = extractPythonScript(SCRIPT_NAME, "ccs811_i2c_reader", ".py");

      if (!isScriptRunning()) {
        startScriptWithRetry(scriptFile);
      }

      return readFromSocket();

    } catch (IOException e) {
      log.warn("Failed to extract Python script: {}", SCRIPT_NAME, e);
      return Map.of(); // cleaner than Collections.emptyMap()
    }
  }

  @Override
  protected String getScriptName() {
    return SCRIPT_NAME;
  }

  @Override
  protected Map<String, Number> generateMockData() {
    return Map.of(
        TOTAL_VOLATILE_ORGANIC_COMPOUND_ID, ThreadLocalRandom.current().nextInt(0, 600),
        CARBON_DIOXIDE_ID, ThreadLocalRandom.current().nextInt(400, 2000));
  }
}
