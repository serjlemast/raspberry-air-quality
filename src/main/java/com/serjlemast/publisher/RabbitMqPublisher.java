package com.serjlemast.publisher;

import com.serjlemast.model.Sensor;
import com.serjlemast.publisher.event.SensorEvent;
import java.time.LocalDateTime;
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

  public void publish(Sensor sensor) {
    var event = new SensorEvent(LocalDateTime.now(), sensor);
    log.info("Publishing data: {}", event);
    template.convertAndSend(exchange, routingKey, event);
  }
}
