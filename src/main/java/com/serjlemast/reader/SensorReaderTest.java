package com.serjlemast.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public abstract class SensorReaderTest {

  public static final String TEMPERATURE_CELSIUS_ID = "temperature_celsius";
  public static final String TEMPERATURE_FAHRENHEIT_ID = "temperature_fahrenheit";
  public static final String HUMIDITY_ID = "humidity";
  public static final String CARBON_DIOXIDE_ID = "eco2";
  public static final String TOTAL_VOLATILE_ORGANIC_COMPOUND_ID = "tvoc";

  protected final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${gpio.mock.enabled}")
  protected boolean mockEnable;

  private final AtomicReference<Boolean> scriptStarted = new AtomicReference<>(false);
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  public Map<String, Number> read() {
    if (mockEnable) {
      return generateMockData();
    }

    String scriptName = getScriptName();
    File scriptFile;

    try {
      scriptFile = extractPythonScript(scriptName, scriptName.replace(".py", ""), ".py");
    } catch (IOException e) {
      log.warn("Script file not found: {}", scriptName);
      return Collections.emptyMap();
    }

    if (!scriptStarted.get()) {
      startScriptWithRetry(scriptFile);
    }

    return readFromSocket();
  }

  protected abstract String getScriptName();

  protected Map<String, Number> generateMockData() {
    double tempC = ThreadLocalRandom.current().nextDouble(18, 35);
    double tempF = tempC * 9 / 5 + 32;
    int humidity = ThreadLocalRandom.current().nextInt(25, 55);

    return Map.of(
        TEMPERATURE_CELSIUS_ID, Math.round(tempC * 10.0) / 10.0,
        TEMPERATURE_FAHRENHEIT_ID, Math.round(tempF * 100.0) / 100.0,
        HUMIDITY_ID, humidity);
  }

  private void startScriptWithRetry(File scriptFile) {
    scheduler.scheduleAtFixedRate(
        () -> {
          if (!scriptStarted.get() && scriptFile.exists()) {
            try {
              ProcessBuilder processBuilder =
                  new ProcessBuilder("python3", scriptFile.getAbsolutePath());
              processBuilder.start();
              scriptStarted.set(true);
              log.info("Python script started: {}", scriptFile.getName());
            } catch (IOException e) {
              log.warn("Failed to start Python script. Will retry in 1 minute.");
            }
          }
        },
        0,
        1,
        TimeUnit.MINUTES);
  }

  private Map<String, Number> readFromSocket() {
    try (Socket socket = new Socket("localhost", 5001);
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

      String json = reader.readLine();
      if (json != null && !json.isBlank()) {
        return objectMapper.readValue(json, new TypeReference<>() {});
      }

    } catch (IOException e) {
      log.warn("Failed to read data from socket", e);
    }

    return Collections.emptyMap();
  }

  protected File extractPythonScript(String script, String prefix, String suffix)
      throws IOException {
    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(script)) {
      if (inputStream == null) {
        throw new FileNotFoundException("Python script not found in resources: " + script);
      }

      File tempScriptFile = File.createTempFile(prefix, suffix);
      tempScriptFile.setExecutable(true);
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
