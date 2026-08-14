package com.Santino.Suscripciones.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.Santino.Suscripciones.dto.PagoResponse;
import com.Santino.Suscripciones.service.SuscripcionService;

@Component
public class PagoCompletoListener {

  private final SuscripcionService suscripcionService;

  public PagoCompletoListener(SuscripcionService suscripcionService) {
    this.suscripcionService = suscripcionService;
  }

  @RabbitListener(queues = "${app.rabbitmq.queue.exitoso}")
  public void recibirPagoExitoso(PagoResponse request) {
    System.out.println("Mensaje recibido: El pago fue exitoso");
    suscripcionService.handlePagoExitoso(request);
  }

  @RabbitListener(queues = "${app.rabbitmq.queue.fallido}")
  public void recibirPagoFallido(PagoResponse request) {
    System.out.println("Mensaje recibido: El pago falló");
    suscripcionService.handlePagoFallido(request);
  }

}