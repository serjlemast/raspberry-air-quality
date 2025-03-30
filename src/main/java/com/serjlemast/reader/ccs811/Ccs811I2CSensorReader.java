package com.serjlemast.reader.ccs811;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Ccs811I2CSensorReader {

  public static final String CARBON_DIOXIDE_ID = "eco2";

  public static final String TOTAL_VOLATILE_ORGANIC_COMPOUND_ID = "tvoc";

  public static final String TEMPERATURE_CELSIUS_ID = "temperature_celsius";

  private static final String SCRIPT_NAME = "ccs811_i2c_reader.py";

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${i2c.mock.enabled}")
  private boolean mockEnable;

  public Optional<Map<String, Number>> read() {

    // If mock mode is enabled, return randomly generated sensor data.
    // Otherwise, return an empty Optional (indicating real sensor data should be used).
    if (mockEnable) {
      return Optional.of(generateMockData());
    }

    try {
      File tempScriptFile = extractPythonScript();

      ProcessBuilder processBuilder =
          new ProcessBuilder("sudo", "python3", tempScriptFile.getAbsolutePath());
      Process process = processBuilder.start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      StringBuilder output = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line);
      }

      String jsonResponse = output.toString().trim();
      if (jsonResponse.isEmpty()) {
        log.warn("Empty response from Python script ");
        return Optional.empty();
      }

      if (jsonResponse.contains("A full buffer was not returned")) {
        log.warn("full buffer was not returned");
        return Optional.empty();
      }

      if (jsonResponse.contains("Checksum did not validate")) {
        log.warn("Checksum did not validate");
        return Optional.empty();
      }

      if (jsonResponse.contains("Received unplausible data")) {
        log.warn("Received unplausible data. Try again.");
        return Optional.empty();
      }

      return Optional.ofNullable(objectMapper.readValue(jsonResponse, new TypeReference<>() {}));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Optional.empty();
    }
  }

  private File extractPythonScript() throws IOException {

    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(SCRIPT_NAME)) {
      if (inputStream == null) {
        throw new FileNotFoundException("Python script not found in resources: " + SCRIPT_NAME);
      }

      File tempScriptFile = File.createTempFile("ccs811_i2c_reader", ".py");

      boolean isWritable = tempScriptFile.setWritable(true);

      // Optionally log or handle the result
      if (!isWritable) {
        log.warn("Failed to make the file writable: {}", tempScriptFile.getAbsolutePath());
      }

      try (OutputStream outputStream = new FileOutputStream(tempScriptFile)) {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
          outputStream.write(buffer, 0, length);
        }
      }

      return tempScriptFile;
    }
  }

  private Map<String, Number> generateMockData() {
    double tempC = ThreadLocalRandom.current().nextDouble(18, 35);
    int tvoc = ThreadLocalRandom.current().nextInt(0, 600);
    int eco2 = ThreadLocalRandom.current().nextInt(400, 2000);

    return Map.of(
        TEMPERATURE_CELSIUS_ID,
        Math.round(tempC * 10.0) / 10.0,
        TOTAL_VOLATILE_ORGANIC_COMPOUND_ID,
        tvoc,
        CARBON_DIOXIDE_ID,
        eco2);
  }
}
