package com.serjlemast.model.raspberry;

import java.util.List;

import com.serjlemast.model.sensor.Sensor;

public record Raspberry(List<Sensor> sensors) {}
