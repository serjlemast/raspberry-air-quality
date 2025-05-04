package com.serjlemast.publisher;

import com.serjlemast.message.RaspberrySensorMessage;
import com.serjlemast.model.raspberry.RaspberryInfo;
import com.serjlemast.model.sensor.Sensor;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMqPublisher {

  @Value("${app.rabbitmq.exchange}")
  public String exchange;

  @Value("${app.rabbitmq.routing-key}")
  public String routingKey;

  private final RabbitTemplate template;

  public void publish(RaspberryInfo info, List<Sensor> sensors) {
    log.info("Publishing: info - {}, sensors - {}", info, sensors);
    template.convertAndSend(
        exchange, routingKey, new RaspberrySensorMessage(LocalDateTime.now(), info, sensors));
  }
}
