package com.serjlemast.reader;

import com.serjlemast.reader.ccs811.Ccs811I2CSensorReader;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PythonScriptInitializer {

    private final Ccs811I2CSensorReader reader;

    @PostConstruct
    public void startScript() {
        try {
            File script = reader.extractPythonScript("ccs811_i2c_reader.py", "ccs811_i2c_reader", ".py");

            if (!reader.isScriptRunning()) {
                reader.startScriptWithRetry(script);
                log.info("Started CCS811 Python script.");
            } else {
                log.info("Python script already running.");
            }

        } catch (IOException e) {
            log.error("Could not start Python script", e);
        }
    }
}
