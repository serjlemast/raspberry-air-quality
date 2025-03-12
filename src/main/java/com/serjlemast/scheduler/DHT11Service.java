package com.serjlemast.scheduler;

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
import org.springframework.stereotype.Service;

@Service
public class DHT11Service {
  private static final String SCRIPT_NAME = "dht11_reader.py"; // Название скрипта

  public Map<String, Object> getTemperatureAndHumidity() {
    try {
      // Извлечение скрипта из JAR в временную директорию
      File tempScriptFile = extractPythonScript();

      // Запуск Python-скрипта через ProcessBuilder
      ProcessBuilder processBuilder =
          new ProcessBuilder("python3", tempScriptFile.getAbsolutePath());
      Process process = processBuilder.start();

      // Чтение вывода скрипта
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
      return Map.of("error", "Ошибка при выполнении скрипта");
    }
  }

  // Метод для извлечения скрипта Python из ресурсов
  private File extractPythonScript() throws IOException {
    // Читаем скрипт из ресурсов
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(SCRIPT_NAME);
    if (inputStream == null) {
      throw new FileNotFoundException("Python script not found in resources: " + SCRIPT_NAME);
    }

    // Создаем временный файл
    File tempScriptFile = File.createTempFile("dht11_reader", ".py");
    tempScriptFile.deleteOnExit(); // Удалить файл по завершению

    // Записываем содержимое скрипта в временный файл
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
