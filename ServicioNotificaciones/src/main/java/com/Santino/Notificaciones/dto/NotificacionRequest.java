package com.Santino.Notificaciones.dto;

public record NotificacionRequest(Long suscripcionId, Long pagoId, String estado, String email, String plan) {

}
