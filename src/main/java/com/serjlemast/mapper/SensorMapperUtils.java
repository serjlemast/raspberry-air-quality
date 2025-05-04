package com.serjlemast.mapper;

import com.serjlemast.model.response.SensorDataResponse;
import com.serjlemast.model.sensor.Sensor;
import com.serjlemast.model.sensor.SensorData;
import java.util.List;
import java.util.Map;

public class SensorMapperUtils {

  private SensorMapperUtils() {}

  public static Sensor mapToSensor(SensorDataResponse response) {
    String deviceId = String.format("%s:%03d", response.type().name(), response.devicePin());

    List<SensorData> sensorDataList = mapToSensorDataList(response.data());

    return new Sensor(deviceId, response.type(), sensorDataList);
  }

  private static List<SensorData> mapToSensorDataList(Map<String, Number> dataMap) {
    return dataMap.entrySet().stream()
        .map(entry -> new SensorData(entry.getKey(), entry.getValue()))
        .toList();
  }
}
