package com.serjlemast.service.raspberry;

import com.pi4j.Pi4J;
import com.pi4j.boardinfo.util.BoardInfoHelper;
import com.pi4j.context.Context;
import com.serjlemast.model.raspberry.RaspberryInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RaspberryService {

  @Value("${gpio.mock.enabled}")
  private boolean mockEnable;

  private Context pi4j;

  public RaspberryService() {
    try {
      pi4j = Pi4J.newAutoContext();
    } catch (Exception e) {
      log.error("Pi4j failed to initialize - {}", e.getMessage());
    }
  }

  public RaspberryInfo getInfo() {
    return mockEnable
        ? defaultInfo()
        : new RaspberryInfo(
            pi4j.boardInfo().getBoardModel().getLabel(),
            pi4j.boardInfo().getOperatingSystem().getName(),
            pi4j.boardInfo().getJavaInfo().getVersion(),
            BoardInfoHelper.getJvmMemory().getUsedInMb(),
            BoardInfoHelper.getBoardReading().getTemperatureInCelsius());
  }

  private RaspberryInfo defaultInfo() {
    return new RaspberryInfo("boardModel", "operatingSystem", "javaVersions", 256.0, 40.0);
  }
}
