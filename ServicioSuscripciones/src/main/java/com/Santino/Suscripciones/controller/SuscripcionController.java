package com.Santino.Suscripciones.controller;

import com.Santino.Suscripciones.dto.SuscripcionResponse;
import com.Santino.Suscripciones.dto.UsuarioAutenticado;
import com.Santino.Suscripciones.entity.Suscripcion;
import com.Santino.Suscripciones.exception.ErrorResponse;
import com.Santino.Suscripciones.service.SuscripcionService;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Suscripciones", description = "El usuario sale del JWT, no del body.")
@SecurityRequirement(name = "bearer-jwt")
@RestController
public class SuscripcionController {

    private final SuscripcionService suscripcionService;

    public SuscripcionController(SuscripcionService suscripcionService) {
        this.suscripcionService = suscripcionService;
    }

    @Operation(summary = "Ver suscripción actual")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Suscripción del usuario autenticado"),
            @ApiResponse(responseCode = "404", description = "El usuario no tiene suscripción",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/suscripcion")
    public ResponseEntity<SuscripcionResponse> verSuscripcion(@Parameter(hidden = true) Authentication auth) {
        UsuarioAutenticado usuario = (UsuarioAutenticado) auth.getPrincipal();
        SuscripcionResponse response = suscripcionService.verPlanUsuario(usuario);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Suscribirse a un plan",
            description = "Queda Pendiente y publica `suscripcion.creada`. Pagos cobra después; no esperes el cobro acá.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Suscripción creada en Pendiente"),
            @ApiResponse(responseCode = "404", description = "El plan no existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/suscribirse")
    public ResponseEntity<Suscripcion> suscribirse(
            @Parameter(description = "Nombre del plan", example = "premium") @RequestParam String plan,
            @Parameter(hidden = true) Authentication auth) {

        UsuarioAutenticado usuario = (UsuarioAutenticado) auth.getPrincipal();

        Suscripcion suscripcionGuardada = suscripcionService.crearSuscripcion(plan, usuario);
        return ResponseEntity.ok(suscripcionGuardada);
    }

}
