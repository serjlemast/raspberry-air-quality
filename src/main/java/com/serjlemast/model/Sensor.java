package com.serjlemast.model;

import java.util.List;

public record Sensor(SensorType type, List<SensorData> data) {}
