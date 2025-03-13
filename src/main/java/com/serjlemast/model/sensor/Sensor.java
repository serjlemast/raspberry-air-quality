package com.serjlemast.model.sensor;

import java.util.List;

public record Sensor(SensorType type, List<SensorData> data) {}
