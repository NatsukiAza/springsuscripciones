package com.Santino.Usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Credenciales de login")
public record LoginRequest(
        @Schema(description = "Nombre de usuario", example = "santi") String username,
        @Schema(description = "Password", example = "clave") String password) {

}
