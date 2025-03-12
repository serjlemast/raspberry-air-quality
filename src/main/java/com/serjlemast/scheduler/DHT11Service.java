package com.serjlemast.scheduler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DHT11Service {

  private static final String SCRIPT_NAME = "dht11_reader.py";

  public Map<String, Object> getTemperatureAndHumidity() {
    try {
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream(SCRIPT_NAME);

      if (inputStream == null) {
        return Map.of("error", "Python script not found in resources");
      }

      File tempScriptFile = File.createTempFile(SCRIPT_NAME, ".py");
      tempScriptFile.deleteOnExit();

      // Запись содержимого ресурса в файл
      try (OutputStream outputStream = new FileOutputStream(tempScriptFile)) {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
          outputStream.write(buffer, 0, length);
        }
      }

      // Запуск скрипта через ProcessBuilder
      ProcessBuilder processBuilder =
          new ProcessBuilder("python3", tempScriptFile.getAbsolutePath());
      Process process = processBuilder.start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      StringBuilder output = new StringBuilder();
      String line;

      while ((line = reader.readLine()) != null) {
        output.append(line);
      }

      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(output.toString(), new TypeReference<Map<String, Object>>() {});
    } catch (Exception e) {
      e.printStackTrace();
      return Map.of("error", e.getMessage());
    }
  }
}
