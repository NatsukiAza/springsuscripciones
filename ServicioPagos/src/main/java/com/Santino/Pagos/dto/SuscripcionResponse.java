package com.Santino.Pagos.dto;

public record SuscripcionResponse(Long suscripcionId, Long pagoId, String estado, String email, String plan) {

}
