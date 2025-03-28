package com.serjlemast.service;

import com.serjlemast.model.sensor.Sensor;
import java.util.Optional;

public interface SensorService {

  Optional<Sensor> readSensorData();
}
