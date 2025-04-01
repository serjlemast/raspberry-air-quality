package com.serjlemast.reader.dht11;

import com.serjlemast.reader.SensorReader;
import java.util.Collections;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Dht11Gpio17SensorReader extends SensorReader {

  private static final String SCRIPT_NAME = "dht11_gpio17_reader.py";

  /*
   * Successful:
   * {temperature_celsius=24.7, temperature_fahrenheit=76.46000000000001, humidity=44},
   * {temperature_celsius=24.6, temperature_fahrenheit=76.28, humidity=44}.
   *
   * error:
   * {error=Checksum did not validate. Try again.}.
   */
  @Override
  public Map<String, Number> read() {
    try {
      // If mock mode is enabled, return randomly generated sensor data.
      // Otherwise, return an empty Optional (indicating real sensor data should be used).
      if (mockEnable) {
        return generateMockData();
      }

      var tempScriptFile = extractPythonScript(SCRIPT_NAME, "dht11_gpio17_reader", ".py");

      return readScript(tempScriptFile);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Collections.emptyMap();
    }
  }
}
