package com.serjlemast.scheduler;

import com.pi4j.Pi4J;
import com.pi4j.boardinfo.util.BoardInfoHelper;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.util.Console;
import com.serjlemast.model.SensorDataEvent;
import com.serjlemast.publisher.RabbitMqPublisher;
import com.serjlemast.service.SensorService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SchedulerProcessor {

  private final RabbitMqPublisher publisher;
  private final List<SensorService> sensorServices;

  private final Context pi4j = Pi4J.newAutoContext();
  private final DigitalOutput digitalOutput32 = pi4j.digitalOutput().create(32);

    public SchedulerProcessor(RabbitMqPublisher publisher, List<SensorService> sensorServices) {
        this.publisher = publisher;
        this.sensorServices = sensorServices;

      digitalOutput32.addListener(
              e -> {
                log.info("test  {}", e.toString());
              });
    }


    @SneakyThrows
  @Scheduled(cron = "${scheduled.cron}")
  public void process() {

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
//        "Board temperature (°C): " + BoardInfoHelper.getBoardReading().getTemperatureInCelsius());

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

    wrapper(
        () -> {
          var sensors = sensorServices.stream().map(SensorService::readSensors).toList();
          var event = new SensorDataEvent(timestamp, sensors);
          publisher.publish(event);
        });
  }

  private void wrapper(Runnable r) {
    try {
      r.run();
    } catch (Exception e) {
      log.error("Error writing sensor data", e);
    }
  }
}
