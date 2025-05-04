package com.serjlemast.model.sensor;

import java.util.List;

public record Sensor(String deviceId, SensorType type, List<SensorData> data) {}
