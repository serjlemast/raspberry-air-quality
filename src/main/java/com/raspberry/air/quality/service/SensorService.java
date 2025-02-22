package com.raspberry.air.quality.service;

import com.raspberry.air.quality.model.SensorData;

public interface SensorService {

  SensorData readSensors();
}
