package com.serjlemast.configuration;

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
   * ConnectionFactory implementation that (when the cache mode is CachingConnectionFactory.CacheMode.CHANNEL (default))
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
}
