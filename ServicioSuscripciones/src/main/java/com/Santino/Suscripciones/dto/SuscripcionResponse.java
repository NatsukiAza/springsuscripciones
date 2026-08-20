package com.Santino.Suscripciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Suscripción del usuario autenticado")
public record SuscripcionResponse(
        @Schema(description = "Id de la suscripción", example = "50") Long id,
        @Schema(description = "Id del usuario (claim del JWT)", example = "7") Long idUser,
        @Schema(description = "Id del plan", example = "1") Long idPlan,
        @Schema(description = "Pendiente, Activo o Pago rechazado", example = "Pendiente") String estado) {

}
