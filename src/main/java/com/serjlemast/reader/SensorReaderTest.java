package com.serjlemast.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

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

  private volatile boolean scriptStarted = false;

  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

  @Value("${sensor.i2c.socket.host}")
  private String sensorHost;

  @Value("${sensor.i2c.socket.port}")
  private int sensorPort;

  protected abstract Map<String, Number> read();

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

  protected void startScriptWithRetry(File scriptFile) {
    scheduler.scheduleAtFixedRate(
        () -> {
          if (!scriptStarted && scriptFile.exists()) {
            try {
              new ProcessBuilder("python3", scriptFile.getAbsolutePath()).start();
              log.info("Attempted to start Python script: {}", scriptFile.getName());
            } catch (IOException e) {
              log.warn("Failed to start Python script. Will retry in 1 minute.", e);
            }
          }
        },
        0,
        1,
        TimeUnit.MINUTES);
  }

  protected Map<String, Number> readFromSocket() {
    try (Socket socket = new Socket(sensorHost, sensorPort);
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

      String json = reader.readLine();
      if (json != null && !json.isBlank()) {
        return objectMapper.readValue(json, new TypeReference<>() {});
      }

    } catch (IOException e) {
      log.warn("Failed to read data from socket at {}:{}.", sensorHost, sensorPort, e);
    }

    return Collections.emptyMap();
  }

  protected File extractPythonScript(String resourceName, String prefix, String suffix)
      throws IOException {
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
    if (inputStream == null) {
      throw new FileNotFoundException("Python script not found in resources: " + resourceName);
    }

    File tempScriptFile = File.createTempFile(prefix, suffix);
    tempScriptFile.setExecutable(true);

    try (inputStream;
        OutputStream outputStream = new FileOutputStream(tempScriptFile)) {
      inputStream.transferTo(outputStream);
    }

    return tempScriptFile;
  }

  public boolean isScriptRunning() {
    try (Socket socket = new Socket(sensorHost, sensorPort)) {
      scriptStarted = true;
      return true;
    } catch (IOException e) {
      scriptStarted = false;
      return false;
    }
  }
}
