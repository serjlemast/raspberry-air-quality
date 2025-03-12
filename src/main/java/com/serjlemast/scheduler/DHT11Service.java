package com.serjlemast.scheduler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DHT11Service {
  private static final String SCRIPT_NAME = "dht11_reader.py"; // Название скрипта

  public Map<String, Object> getTemperatureAndHumidity() {
    try {

      String jarPath =
          DHT11Service.class.getProtectionDomain().getCodeSource().getLocation().getPath();
      File jarFile = new File(jarPath);
      File scriptFile = new File(jarFile.getParent(), SCRIPT_NAME);

      if (!scriptFile.exists()) {
        return Map.of("error", "Python script not found: " + scriptFile.getAbsolutePath());
      }

      ProcessBuilder processBuilder = new ProcessBuilder("python3", scriptFile.getAbsolutePath());
      Process process = processBuilder.start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      StringBuilder output = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line);
      }

      String jsonResponse = output.toString().trim();
      if (jsonResponse.isEmpty()) {
        return Map.of("error", "Empty response from Python script");
      }

      // Парсим JSON
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(jsonResponse, new TypeReference<Map<String, Object>>() {});

    } catch (Exception e) {
      e.printStackTrace();
      return Map.of("error", e.getMessage());
    }
  }
}
