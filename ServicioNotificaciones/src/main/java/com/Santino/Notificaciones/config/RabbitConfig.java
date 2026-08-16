package com.Santino.Notificaciones.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Value("${app.rabbitmq.exchange.pagos}")
    public String pagosExchangeName;

    @Value("${app.rabbitmq.queue.notificacion}")
    public String NotificacionQueueName;

    @Value("${app.rabbitmq.routing-key.exitoso}")
    public String pagoExitosoRoutingKey;

    @Value("${app.rabbitmq.routing-key.fallido}")
    public String pagoFallidoRoutingKey;

    @Bean
    public TopicExchange pagosExchange() {
        return new TopicExchange(pagosExchangeName, true, false);
    }

    @Bean
    public Queue notificacionCreadaQueue() {
        return new Queue(NotificacionQueueName, true);
    }

    @Bean
    public Binding bindingPagoFallido(Queue notificacionCreadaQueue, TopicExchange pagosExchange) {
        return BindingBuilder
                .bind(notificacionCreadaQueue)
                .to(pagosExchange)
                .with(pagoFallidoRoutingKey);
    }

    @Bean
    public Binding bindingPagoExitoso(Queue notificacionCreadaQueue, TopicExchange pagosExchange) {
        return BindingBuilder
                .bind(notificacionCreadaQueue)
                .to(pagosExchange)
                .with(pagoExitosoRoutingKey);
    }
}