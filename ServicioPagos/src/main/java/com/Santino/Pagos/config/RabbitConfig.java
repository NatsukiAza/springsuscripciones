package com.Santino.Pagos.config;

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

    @Value("${app.rabbitmq.exchange.suscripcion}")
    public String suscripcionExchangeName;

    @Value("${app.rabbitmq.queue.exitoso}")
    public String pagoExitosoQueueName;

    @Value("${app.rabbitmq.routing-key.exitoso}")
    public String pagoExitosoRoutingKey;

    @Value("${app.rabbitmq.queue.fallido}")
    public String pagoFallidoQueueName;

    @Value("${app.rabbitmq.routing-key.fallido}")
    public String pagoFallidoRoutingKey;

    @Value("${app.rabbitmq.queue.suscripcion-creada}")
    public String suscripcionCreadaQueueName;

    @Value("${app.rabbitmq.routing-key.suscripcion-creada}")
    public String suscripcionRoutingKey;

    @Value("${app.rabbitmq.queue.notificacion}")
    public String suscripcionNotifQueueName;

    @Bean
    public TopicExchange pagosExchange() {
        return new TopicExchange(pagosExchangeName, true, false);
    }

    @Bean
    public TopicExchange suscripcionExchange() {
        return new TopicExchange(suscripcionExchangeName, true, false);
    }

    @Bean
    public Queue suscripcionCreadaQueue() {
        return new Queue(suscripcionCreadaQueueName, true);
    }

    @Bean
    public Queue PagoNotifQueue() {
        return new Queue(suscripcionNotifQueueName, true);
    }

    @Bean
    public Queue pagoExitosoQueue() {
        return new Queue(pagoExitosoQueueName, true);
    }

    @Bean
    public Queue pagoFallidoQueue() {
        return new Queue(pagoFallidoQueueName, true);
    }

    @Bean
    public Binding bindingPagoExitoso(Queue pagoExitosoQueue, TopicExchange pagosExchange) {
        return BindingBuilder
                .bind(pagoExitosoQueue)
                .to(pagosExchange)
                .with(pagoExitosoRoutingKey);
    }

    @Bean
    public Binding bindingPagoFallido(Queue pagoFallidoQueue, TopicExchange pagosExchange) {
        return BindingBuilder
                .bind(pagoFallidoQueue)
                .to(pagosExchange)
                .with(pagoFallidoRoutingKey);
    }

    @Bean
    public Binding bindingSuscripcionCreada(Queue suscripcionCreadaQueue, TopicExchange suscripcionExchange) {
        return BindingBuilder
                .bind(suscripcionCreadaQueue)
                .to(suscripcionExchange)
                .with(suscripcionRoutingKey);
    }

    @Bean
    public Binding bindingSuscripcionNotificadaExitosa(Queue PagoNotifQueue, TopicExchange pagosExchange) {
        return BindingBuilder
                .bind(PagoNotifQueue)
                .to(pagosExchange)
                .with(pagoExitosoRoutingKey);
    }

    @Bean
    public Binding bindingSuscripcionNotificadaFallida(Queue PagoNotifQueue, TopicExchange pagosExchange) {
        return BindingBuilder
                .bind(PagoNotifQueue)
                .to(pagosExchange)
                .with(pagoFallidoRoutingKey);
    }
}