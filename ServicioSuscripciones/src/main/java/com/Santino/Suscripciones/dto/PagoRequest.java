package com.Santino.Suscripciones.dto;

public record PagoRequest(Long userID, String email, Long suscripcionID, Long monto, String plan) {

}
