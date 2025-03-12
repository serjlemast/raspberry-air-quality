package com.serjlemast.service;

import java.util.Optional;

import com.serjlemast.model.SensorData;

public interface SensorService {

  Optional<SensorData> readSensors();
}
