package com.serjlemast.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public abstract class SensorReaderTest {

    public static final String CARBON_DIOXIDE_ID = "eco2";

    public static final String TOTAL_VOLATILE_ORGANIC_COMPOUND_ID = "tvoc";

    @Value("${gpio.mock.enabled}")
    protected boolean mockEnable;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicReference<Map<String, Number>> latestData = new AtomicReference<>();

    protected abstract String getScriptName();

    protected abstract Map<String, Number> generateMockData();

    protected void startScriptIfNeeded() {
        if (started.compareAndSet(false, true)) {
            Thread thread = new Thread(() -> {
                try {
                    Process process = new ProcessBuilder("python3", getScriptName())
                            .redirectErrorStream(true)
                            .start();

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            Map<String, Number> parsed = objectMapper.readValue(line, new TypeReference<>() {});
                            latestData.set(parsed);
                        }
                    }

                } catch (IOException e) {
                    log.error("Failed to start or read from script: {}", getScriptName(), e);
                }
            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    public Map<String, Number> read() {
        if (mockEnable) {
            return generateMockData();
        }

        startScriptIfNeeded();
        return latestData.get();
    }
}
