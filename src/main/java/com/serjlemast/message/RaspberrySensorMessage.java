package com.serjlemast.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.serjlemast.model.raspberry.RaspberryInfo;
import com.serjlemast.model.sensor.Sensor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a message containing data from Raspberry Pi sensors
 *
 * @param timestamp The timestamp when the message was recorded
 * @param info Information about the Raspberry Pi device
 * @param sensors The list of sensor data captured at the given timestamp
 */
public record RaspberrySensorMessage(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,
    RaspberryInfo info,
    List<Sensor> sensors) {}
