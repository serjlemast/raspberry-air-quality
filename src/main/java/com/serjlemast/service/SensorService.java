package com.serjlemast.service;

import com.serjlemast.model.Sensor;
import java.util.Optional;

public interface SensorService {

  Optional<Sensor> readSensor();
}
