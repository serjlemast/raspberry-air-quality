package com.serjlemast.scheduler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DHT11Service {
  private static final String SCRIPT_NAME = "dht11_reader.py";

  public Map<String, Object> getTemperatureAndHumidity() {
    try {
      String jarPath =
          DHT11Service.class.getProtectionDomain().getCodeSource().getLocation().getPath();
      jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);
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

      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(output.toString(), new TypeReference<>() {});
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Map.of("error", e.getMessage());
    }
  }
}
