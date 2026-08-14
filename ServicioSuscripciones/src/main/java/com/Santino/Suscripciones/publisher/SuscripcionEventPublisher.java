package com.Santino.Suscripciones.publisher;

import com.Santino.Suscripciones.config.RabbitConfig;

import com.Santino.Suscripciones.dto.PagoRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class SuscripcionEventPublisher {

  private final RabbitTemplate rabbitTemplate;
  private final RabbitConfig rabbitConfig;

  public SuscripcionEventPublisher(RabbitTemplate rabbitTemplate, RabbitConfig rabbitConfig) {
    this.rabbitTemplate = rabbitTemplate;
    this.rabbitConfig = rabbitConfig;
  }

  public void publicarSuscripcion(PagoRequest suscripcion) {

    rabbitTemplate.convertAndSend(rabbitConfig.suscripcionExchangeName, rabbitConfig.suscripcionRoutingKey,
        suscripcion);

    System.out.println("Evento publicado: suscripcion con id: " + suscripcion.suscripcionID() + " enviada a pagos.");

  }

}
