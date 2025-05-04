package com.serjlemast.service.collector;

import com.serjlemast.mapper.SensorMapperUtils;
import com.serjlemast.model.response.SensorDataResponse;
import com.serjlemast.model.sensor.Sensor;
import com.serjlemast.service.url.UrlService;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class SensorDataCollectorService {

  private final RestClient restClient;

  private final UrlService urlService;

  public SensorDataCollectorService(RestClient restClient, UrlService urlService) {
    this.restClient = restClient;
    this.urlService = urlService;
  }

  public Optional<List<Sensor>> findAllInformationAboutSensors() {
    if (urlService.isEmpty()) {
      log.warn("No sensors detected for the Raspberry Pi controller");
      return Optional.empty();
    }

    try {
      return Optional.of(collectSensorData(urlService.getUrls()));
    } catch (Exception e) {
      log.warn("Error processing sensor data - {}", e.getMessage());
      return Optional.empty();
    }
  }

  private List<Sensor> collectSensorData(Set<String> urls) {
    return urls.stream().map(this::fetchSensorData).map(SensorMapperUtils::mapToSensor).toList();
  }

  private SensorDataResponse fetchSensorData(String url) {
    return restClient.get().uri(url).retrieve().body(SensorDataResponse.class);
  }
}
