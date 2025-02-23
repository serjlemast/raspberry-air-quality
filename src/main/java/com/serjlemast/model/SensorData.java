package com.serjlemast.model;

import java.util.List;

public record SensorData(SensorType sensorType, List<Sensor> sensors) {}
