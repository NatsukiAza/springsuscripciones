package com.Santino.Suscripciones.dto;

public record PagoResponse(Long suscripcionId, Long pagoId, String estado, String email, Long planId) {

}
