package com.serjlemast.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public abstract class SensorReader {

  public static final String TEMPERATURE_CELSIUS_ID = "temperature_celsius";

  public static final String TEMPERATURE_FAHRENHEIT_ID = "temperature_fahrenheit";

  public static final String HUMIDITY_ID = "humidity";

  public static final String CARBON_DIOXIDE_ID = "eco2";

  public static final String TOTAL_VOLATILE_ORGANIC_COMPOUND_ID = "tvoc";

  protected final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${gpio.mock.enabled}")
  protected boolean mockEnable;

  protected abstract Map<String, Number> read();

  protected Map<String, Number> generateMockData() {
    double tempC = ThreadLocalRandom.current().nextDouble(18, 35);
    double tempF = tempC * 9 / 5 + 32;
    int humidity = ThreadLocalRandom.current().nextInt(25, 55);

    return Map.of(
        TEMPERATURE_CELSIUS_ID, Math.round(tempC * 10.0) / 10.0,
        TEMPERATURE_FAHRENHEIT_ID, Math.round(tempF * 100.0) / 100.0,
        HUMIDITY_ID, humidity);
  }

  protected Map<String, Number> readScript(File tempScriptFile) throws IOException {
    var processBuilder = new ProcessBuilder("sudo", "python3", tempScriptFile.getAbsolutePath());
    var process = processBuilder.start();

    var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    var output = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      output.append(line);
    }

    String jsonResponse = output.toString().trim();
    if (jsonResponse.isEmpty()) {
      log.warn("Empty response from Python script ");
      return Collections.emptyMap();
    }

    if (jsonResponse.contains("A full buffer was not returned")) {
      log.warn("full buffer was not returned");
      return Collections.emptyMap();
    }

    if (jsonResponse.contains("Checksum did not validate")) {
      log.warn("Checksum did not validate");
      return Collections.emptyMap();
    }

    if (jsonResponse.contains("Received unplausible data")) {
      log.warn("Received unplausible data. Try again.");
      return Collections.emptyMap();
    }

    return objectMapper.readValue(jsonResponse, new TypeReference<>() {});
  }

  protected File extractPythonScript(String script, String prefix, String suffix)
      throws IOException {

    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(script)) {
      if (inputStream == null) {
        throw new FileNotFoundException("Python script not found in resources: " + script);
      }

      File tempScriptFile = File.createTempFile(prefix, suffix);

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
}
