package com.Santino.Usuario.controller;

import org.springframework.web.bind.annotation.RestController;

import com.Santino.Usuario.dto.UsuarioRequest;
import com.Santino.Usuario.exception.ErrorResponse;
import com.Santino.Usuario.service.UsuarioService;
import com.Santino.Usuario.dto.LoginRequest;
import com.Santino.Usuario.dto.AuthResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth", description = "Registro y login. El JWT de login se usa en los otros servicios.")
@RestController
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Registrar usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario creado"),
            @ApiResponse(responseCode = "409", description = "Username o email ya existen",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/auth/registrarse")
    public ResponseEntity<UsuarioRequest> crearUsuario(@RequestBody UsuarioRequest request) {

        UsuarioRequest usuarioGuardado = usuarioService.crearUsuario(request);
        return ResponseEntity.ok(usuarioGuardado);
    }

    @Operation(summary = "Login", description = "Devuelve un JWT. Copialo y usalo en Authorize de Suscripciones.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token emitido"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> inicioSesion(@RequestBody LoginRequest request) {

        String token = usuarioService.iniciarSesion(request);

        return ResponseEntity.ok(new AuthResponse(token));
    }

}
