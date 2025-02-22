package com.serjlemast.publisher;

import com.serjlemast.model.SensorData;
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

  public void publish(SensorData data) {
    log.info("Publishing data: {}", data);
    template.convertAndSend(exchange, routingKey, data);
  }
}
