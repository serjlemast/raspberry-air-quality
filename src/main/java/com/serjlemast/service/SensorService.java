package com.serjlemast.service;

import com.serjlemast.model.sensor.Sensor;
import com.serjlemast.model.sensor.SensorData;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SensorService {

  Optional<Sensor> readSensorData();

  List<SensorData> fetchSensorData(Map<String, Number> data);
}
