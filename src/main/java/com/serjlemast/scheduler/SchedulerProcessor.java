package com.serjlemast.scheduler;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.serjlemast.publisher.RabbitMqPublisher;
import com.serjlemast.service.SensorService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerProcessor {

  private final RabbitMqPublisher publisher;
  private final List<SensorService> sensorServices;

  private static final int GPIO_PIN_4 = 4;

  public void test() {
    Context pi4j = Pi4J.newAutoContext();
    log.info(" >>> 1 Starting SchedulerProcessor");
    // Create digital output for controlling DHT11 sensor
    DigitalOutput output =
        pi4j.create(
            DigitalOutputConfig.newBuilder(pi4j)
                .id("DHT11_OUTPUT")
                .name("DHT11 Output")
                .address(GPIO_PIN_4)
                .onState(com.pi4j.io.gpio.digital.DigitalState.HIGH)
                .build());

    // Send signal to DHT11 sensor
    output.low();
    try {
      Thread.sleep(18);
    } catch (InterruptedException ignored) {
    }
    output.high();
    try {
      Thread.sleep(1);
    } catch (InterruptedException ignored) {
    }
    pi4j.shutdown();

    log.info(" >>> 2 Starting SchedulerProcessor");
    pi4j = Pi4J.newAutoContext(); // Re-initialize Pi4J context
    try {
      Thread.sleep(1);
    } catch (InterruptedException ignored) {
    }
    // After sending, now switch to input to read data
    DigitalInput input =
        pi4j.create(
            DigitalInputConfig.newBuilder(pi4j)
                .id("DHT11_INPUT")
                .name("DHT11 Input")
                .address(GPIO_PIN_4) // Same GPIO pin
                //                .pull(PullResistance.OFF) // No pull-up or pull-down resistor
                .build());
    try {
      Thread.sleep(1);
    } catch (InterruptedException ignored) {
    }
    // Wait for response from DHT11 sensor
    while (input.state() == com.pi4j.io.gpio.digital.DigitalState.HIGH) {}
    while (input.state() == com.pi4j.io.gpio.digital.DigitalState.LOW) {}
    while (input.state() == com.pi4j.io.gpio.digital.DigitalState.HIGH) {}

    log.info(" >>> 3 Starting SchedulerProcessor, input.state() - {}", input.state());

    // Read 40 bits of data (Temperature and Humidity)
    int[] data = new int[40];
    for (int i = 0; i < 40; i++) {
      while (input.state() == com.pi4j.io.gpio.digital.DigitalState.LOW) {}
      long startTime = System.nanoTime();
      while (input.state() == com.pi4j.io.gpio.digital.DigitalState.HIGH) {}
      long pulseTime = System.nanoTime() - startTime;
      data[i] = (pulseTime > 50000) ? 1 : 0; // 1 if pulse > 50ms, else 0
    }

    log.info(
        " >>> 3.1 Starting SchedulerProcessor, input.state()- {}, data - {}",
        input.state(),
        Arrays.toString(data));

    // Decode the data
    int humidityInt = bitsToByte(data, 0);
    int humidityDec = bitsToByte(data, 8);
    int temperatureInt = bitsToByte(data, 16);
    int temperatureDec = bitsToByte(data, 24);
    int checksum = bitsToByte(data, 32);

    log.info(" >>> 4 Starting SchedulerProcessor");

    int calculatedChecksum = humidityInt + humidityDec + temperatureInt + temperatureDec;
    if ((calculatedChecksum & 0xFF) == checksum) {
      System.out.println("Data received successfully:");
      System.out.println("Temperature: " + temperatureInt + "." + temperatureDec + "°C");
      System.out.println("Humidity: " + humidityInt + "." + humidityDec + "%");
    } else {
      System.err.println("Checksum error!");
    }

    // Shutdown Pi4J context
    pi4j.shutdown();
  }

  private static int bitsToByte(int[] data, int start) {
    int value = 0;
    for (int i = 0; i < 8; i++) {
      value <<= 1;
      value |= data[start + i];
    }
    return value;
  }

  //    @Scheduled(cron = "${scheduled.cron}")
  @Scheduled(cron = "*/10 * * * * *")
  public void process() {

    test();

    // Initialize a HumiTempComponent with default values
    //        final var dht11 = new HumiTempComponent();
    //
    //        log.info("Welcome to the HumiTempApp");
    //        log.info("Measurement starts now.. ");
    //
    //        // Start some measurements in a loop
    //        for (int i = 0; i < 5; i++) {
    //            log.info("It is currently " + dht11.getTemperature() + "°C and the
    // Humidity is " + dht11.getHumidity() + "%.");
    //            sleep(2000);
    //        }

    //    // Create Pi4J console wrapper/helper
    //    // (This is a utility class to abstract some of the boilerplate stdin/stdout code)
    //    final var console = new Console();
    //
    //    // Print program title/header
    //    console.title("<-- The Pi4J Project -->", "Minimal Example project");
    //
    //    PrintInfo.printLoadedPlatforms(console, pi4j);
    //    PrintInfo.printDefaultPlatform(console, pi4j);
    //    PrintInfo.printProviders(console, pi4j);
    //
    //    // ------------------------------------------------------------
    //    // Output Pi4J Board information
    //    // ------------------------------------------------------------
    //    // When the Pi4J Context is initialized, a board detection is
    //    // performed. You can use this info in case you need board-specific
    //    // functionality.
    //    // OPTIONAL
    //    console.println("Board model: " + pi4j.boardInfo().getBoardModel().getLabel());
    //    console.println("Operating system: " + pi4j.boardInfo().getOperatingSystem());
    //    console.println("Java versions: " + pi4j.boardInfo().getJavaInfo());
    //    // This info is also available directly from the BoardInfoHelper,
    //    // and with some additional realtime data.
    //    console.println("Board model: " + BoardInfoHelper.current().getBoardModel().getLabel());
    //    console.println(
    //        "Raspberry Pi model with RP1 chip (Raspberry Pi 5): " + BoardInfoHelper.usesRP1());
    //    console.println("OS is 64-bit: " + BoardInfoHelper.is64bit());
    //    console.println("JVM memory used (MB): " + BoardInfoHelper.getJvmMemory().getUsedInMb());
    //    console.println(
    //        "Board temperature (°C): " +
    // BoardInfoHelper.getBoardReading().getTemperatureInCelsius());

    // Here we will create the I/O interface for a LED with minimal code.

    //    digitalOutput32.addListener(
    //        e -> {
    //          log.info("test  {}", e.toString());
    //        });

    // OPTIONAL: print the registry
    //    PrintInfo.printRegistry(console, pi4j);

    //    while (pressCount < 5) {
    //      if (led.state() == DigitalState.HIGH) {
    //        console.println("LED low");
    //        led.low();
    //      } else {
    //        console.println("LED high");
    //        led.high();
    //      }
    //      Thread.sleep(500 / (pressCount + 1));
    //    }

    var timestamp = LocalDateTime.now();
    var threadName = Thread.currentThread().getName();
    //    log.info("Starting sensor data processing at {} on thread: {}", timestamp, threadName);

    //    wrapper(
    //        () -> {
    //          var sensors = sensorServices.stream().map(SensorService::readSensors).toList();
    //          var event = new SensorDataEvent(timestamp, sensors);
    //          publisher.publish(event);
    //        });
  }

  private void wrapper(Runnable r) {
    try {
      r.run();
    } catch (Exception e) {
      log.error("Error writing sensor data", e);
    }
  }
}
