package com.Santino.Notificaciones.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Controller;

import com.Santino.Notificaciones.dto.NotificacionRequest;
import com.Santino.Notificaciones.service.NotificacionService;

@Controller
public class SuscripcionEventListener {

  private final NotificacionService notifService;

  public SuscripcionEventListener(NotificacionService notifService) {
    this.notifService = notifService;
  }

  @RabbitListener(queues = "${app.rabbitmq.queue.notificacion}")
  public void recibirSuscripcion(NotificacionRequest request) {

    System.out.println("Mensaje recibido");
    notifService.enviarNotificacion(request);
  }

}
