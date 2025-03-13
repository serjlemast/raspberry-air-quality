package com.serjlemast.gpio;

import java.util.Map;
import java.util.Optional;

public interface SensorReader {

  Optional<Map<String, Number>> read();
}
