package com.serjlemast.reader.ccs811;

import com.serjlemast.reader.SensorReaderTest;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import jakarta.annotation.PostConstruct;
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
    return readFromSocket();
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

  @PostConstruct
  public void startScriptOnce() {
    try {
      File scriptFile = extractPythonScript("ccs811_i2c_reader.py", "ccs811_i2c_reader", ".py");

      if (!isScriptRunning()) {
        startScriptWithRetry(scriptFile);
        log.info("Started CCS811 Python script.");
      }
    } catch (IOException e) {
      log.error("Failed to start CCS811 Python script.", e);
    }
  }
}
