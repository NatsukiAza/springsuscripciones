package com.Santino.Notificaciones.service;

import org.springframework.stereotype.Service;

import com.Santino.Notificaciones.dto.NotificacionRequest;

@Service
public class NotificacionService {

  public void enviarNotificacion(NotificacionRequest request) {
    System.out.println(request.email() + ": Se ha " + ("Exitoso".equals(request.estado()) ? "realizado"
        : "rechazado") + " un pago para el plan " + request.plan());
  }

}
