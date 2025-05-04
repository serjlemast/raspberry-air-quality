package com.serjlemast.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serjlemast.model.sensor.SensorType;
import java.util.Map;
import lombok.Builder;

@Builder
public record SensorDataResponse(
    @JsonProperty("device-pin") int devicePin, SensorType type, Map<String, Number> data) {}
