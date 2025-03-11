package com.serjlemast.scheduler;

import com.pi4j.Pi4J;
import com.pi4j.boardinfo.util.BoardInfoHelper;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.platform.Platforms;
import com.pi4j.util.Console;
import com.serjlemast.model.SensorDataEvent;
import com.serjlemast.publisher.RabbitMqPublisher;
import com.serjlemast.service.SensorService;
import java.time.LocalDateTime;
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

  private final Context pi4j = Pi4J.newAutoContext();

  @Scheduled(cron = "${scheduled.cron}")
  public void process() {

    // Create Pi4J console wrapper/helper
    // (This is a utility class to abstract some of the boilerplate stdin/stdout code)
    final var console = new Console();

    // Print program title/header
    console.title("<-- The Pi4J Project -->", "Minimal Example project");

    // ************************************************************
    //
    // WELCOME TO Pi4J:
    //
    // Here we will use this getting started example to
    // demonstrate the basic fundamentals of the Pi4J library.
    //
    // This example is to introduce you to the boilerplate
    // logic and concepts required for all applications using
    // the Pi4J library.  This example will do use some basic I/O.
    // Check the pi4j-examples project to learn about all the I/O
    // functions of Pi4J.
    //
    // ************************************************************

    // ------------------------------------------------------------
    // Initialize the Pi4J Runtime Context
    // ------------------------------------------------------------
    // Before you can use Pi4J you must initialize a new runtime
    // context.
    //
    // The 'Pi4J' static class includes a few helper context
    // creators for the most common use cases.  The 'newAutoContext()'
    // method will automatically load all available Pi4J
    // extensions found in the application's classpath which
    // may include 'Platforms' and 'I/O Providers'
    var pi4j = Pi4J.newAutoContext();

    // ------------------------------------------------------------
    // Output Pi4J Context information
    // ------------------------------------------------------------
    // The created Pi4J Context initializes platforms, providers
    // and the I/O registry. To help you to better understand this
    // approach, we print out the info of these. This can be removed
    // from your own application.
    // OPTIONAL
    PrintInfo.printLoadedPlatforms(console, pi4j);
    PrintInfo.printDefaultPlatform(console, pi4j);
    PrintInfo.printProviders(console, pi4j);

    // ------------------------------------------------------------
    // Output Pi4J Board information
    // ------------------------------------------------------------
    // When the Pi4J Context is initialized, a board detection is
    // performed. You can use this info in case you need board-specific
    // functionality.
    // OPTIONAL
    console.println("Board model: " + pi4j.boardInfo().getBoardModel().getLabel());
    console.println("Operating system: " + pi4j.boardInfo().getOperatingSystem());
    console.println("Java versions: " + pi4j.boardInfo().getJavaInfo());
    // This info is also available directly from the BoardInfoHelper,
    // and with some additional realtime data.
    console.println("Board model: " + BoardInfoHelper.current().getBoardModel().getLabel());
    console.println("Raspberry Pi model with RP1 chip (Raspberry Pi 5): " + BoardInfoHelper.usesRP1());
    console.println("OS is 64-bit: " + BoardInfoHelper.is64bit());
    console.println("JVM memory used (MB): " + BoardInfoHelper.getJvmMemory().getUsedInMb());
    console.println("Board temperature (°C): " + BoardInfoHelper.getBoardReading().getTemperatureInCelsius());

    // Here we will create the I/O interface for a LED with minimal code.
    var led = pi4j.digitalOutput().create(7);

    // The button needs a bit more configuration, so we use a config builder.
    var buttonConfig = DigitalInput.newConfigBuilder(pi4j)
            .id("sensor")
            .name("Temp")
//            .address(PIN_BUTTON)
//            .pull(PullResistance.PULL_DOWN)
            .debounce(3000L);
    var button = pi4j.create(buttonConfig);
    button.addListener(e -> {

        console.println("Button was pressed for the " + "th time: " + e);

    });

    // OPTIONAL: print the registry
    PrintInfo.printRegistry(console, pi4j);

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
    log.info("Starting sensor data processing at {} on thread: {}", timestamp, threadName);

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
