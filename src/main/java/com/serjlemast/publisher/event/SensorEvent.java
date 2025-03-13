package com.serjlemast.publisher.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serjlemast.model.Sensor;

import java.time.LocalDateTime;

public record SensorEvent(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,
    Sensor sensor) {}
