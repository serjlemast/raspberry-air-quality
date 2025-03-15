package com.serjlemast.publisher.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serjlemast.model.raspberry.RaspberryInfo;
import com.serjlemast.model.sensor.Sensor;
import java.time.LocalDateTime;
import java.util.List;

public record RaspberryEvent(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,
    RaspberryInfo info,
    List<Sensor> sensors) {}
