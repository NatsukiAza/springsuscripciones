package com.Santino.Suscripciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Alta de un plan de streaming")
public record PlanRequest(
        @Schema(description = "Nombre único del plan", example = "premium") String nombre,
        @Schema(description = "Qué incluye", example = "Full HD") String descripcion,
        @Schema(description = "Costo en la moneda del dominio (entero)", example = "1500") Long costo) {
}
