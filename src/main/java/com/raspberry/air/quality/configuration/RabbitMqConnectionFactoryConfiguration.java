package com.raspberry.air.quality.configuration;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConnectionFactoryConfiguration {

  @Value("${app.rabbitmq.username}")
  private String username;

  @Value("${app.rabbitmq.password}")
  private String password;

  @Value("${app.rabbitmq.host}")
  private String host;

  @Value("${app.rabbitmq.port}")
  private Integer port;

  @Value("${app.rabbitmq.virtualhost}")
  private String virtualHost;

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  /*
   * ConnectionFactory implementation that (when the cache mode is CachingConnectionFactory.CacheMode.CHANNEL (default)
   * returns the same Connection from all createConnection() calls,
   * and ignores calls to com.rabbitmq.client.Connection.close() and caches Channel.
   */
  @Bean
  public ConnectionFactory connectionFactory() {
    var connectionFactory = new CachingConnectionFactory();
    connectionFactory.setVirtualHost(virtualHost);
    connectionFactory.setHost(host);
    connectionFactory.setUsername(username);
    connectionFactory.setPort(port);
    connectionFactory.setPassword(password);
    return connectionFactory;
  }

  //  @Bean
  //  public AmqpAdmin amqpAdmin() {
  //    return new RabbitAdmin(connectionFactory());
  //  }

  @Bean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
    var factory = new SimpleRabbitListenerContainerFactory();

    /*
     * Tell the broker how many messages to send to each consumer in a single request.
     * Often this can be set quite high to improve throughput.
     *
     * Request a specific prefetchCount "quality of service" settings for this channel.
     * Note the prefetch count must be between 0 and 65535 (unsigned short in AMQP 0-9-1).
     *
     * Params:  prefetchCount – maximum number of messages that the server will deliver, 0 if unlimited
     *         global – true if the settings should be applied to the entire channel rather than each consumer
     */
    factory.setPrefetchCount(1);

    factory.setConnectionFactory(connectionFactory());
    factory.setMessageConverter(jsonMessageConverter());
    /*
     * The ack knowledge mode to set.
     * Defaults to AcknowledgeMode. AUTO
     * NONE - No acks - autoAck=true in Channel. basicConsume().
     * MANUAL - Manual acks - user must ack/ nack via a channel aware listener.
     * AUTO - Auto - the container will issue the ack/ nack based on whether the listener returns normally,
     *        or throws an exception
     */
    factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
    return factory;
  }
}
