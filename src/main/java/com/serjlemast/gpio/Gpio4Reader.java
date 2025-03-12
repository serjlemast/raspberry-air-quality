package com.serjlemast.gpio;

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
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Gpio4Reader {

  public static final String TEMPERATURE_CELSIUS_ID = "temperature_celsius";

  public static final String TEMPERATURE_FAHRENHEIT_ID = "temperature_fahrenheit";

  public static final String HUMIDITY_ID = "humidity";

  private static final String SCRIPT_NAME = "dht11_reader.py";

  private final ObjectMapper objectMapper = new ObjectMapper();

  /*
   * Successful:
   * {temperature_celsius=24.7, temperature_fahrenheit=76.46000000000001, humidity=44},
   * {temperature_celsius=24.6, temperature_fahrenheit=76.28, humidity=44}.
   *
   * error:
   * {error=Checksum did not validate. Try again.}.
   */
  public Optional<Map<String, Number>> read() {
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
        log.warn("Empty response from Python script");
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

      File tempScriptFile = File.createTempFile("dht11_reader", ".py");

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
