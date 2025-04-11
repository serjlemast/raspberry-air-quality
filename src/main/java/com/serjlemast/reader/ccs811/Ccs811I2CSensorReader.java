package com.serjlemast.reader.ccs811;

import com.serjlemast.reader.SensorReaderTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class Ccs811I2CSensorReader extends SensorReaderTest {

  @Override
  protected String getScriptName() {
    return "ccs811_i2c_reader.py";
  }

  @Override
  public Map<String, Number> read() {
    return super.read();
  }

  @Override
  protected Map<String, Number> generateMockData() {
    int tvoc = ThreadLocalRandom.current().nextInt(0, 600);
    int eco2 = ThreadLocalRandom.current().nextInt(400, 2000);

    return Map.of(
            TOTAL_VOLATILE_ORGANIC_COMPOUND_ID, tvoc,
            CARBON_DIOXIDE_ID, eco2
    );
  }
}
