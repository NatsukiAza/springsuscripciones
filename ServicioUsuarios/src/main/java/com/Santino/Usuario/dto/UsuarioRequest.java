package com.Santino.Usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Alta de usuario. El mismo shape vuelve en la respuesta, con el password hasheado.")
public record UsuarioRequest(
        @Schema(description = "Nombre de usuario único", example = "santi") String username,
        @Schema(description = "Email único", example = "santi@streamsub.com") String email,
        @Schema(description = "Password en claro al registrar", example = "clave") String password) {
}
