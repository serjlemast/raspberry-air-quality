package com.serjlemast.model;

import java.time.LocalDateTime;
import java.util.List;

public record SensorData(SensorType sensorType, LocalDateTime timestamp, List<Sensor> sensors) {}
