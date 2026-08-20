package com.Santino.Usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT para Authorization: Bearer ... en Suscripciones y Pagos")
public record AuthResponse(
        @Schema(description = "Token firmado", example = "eyJhbGciOiJIUzI1NiJ9...") String token) {

}
