package com.Santino.Suscripciones.controller;

import com.Santino.Suscripciones.dto.SuscripcionResponse;
import com.Santino.Suscripciones.dto.UsuarioAutenticado;
import com.Santino.Suscripciones.entity.Suscripcion;
import com.Santino.Suscripciones.service.SuscripcionService;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@RestController
public class SuscripcionController {

    private final SuscripcionService suscripcionService;

    public SuscripcionController(SuscripcionService suscripcionService) {
        this.suscripcionService = suscripcionService;
    }

    @GetMapping("/suscripcion")
    public ResponseEntity<SuscripcionResponse> verSuscripcion(Authentication auth) {
        UsuarioAutenticado usuario = (UsuarioAutenticado) auth.getPrincipal();
        SuscripcionResponse response = suscripcionService.verPlanUsuario(usuario);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/suscribirse")
    public ResponseEntity<Suscripcion> suscribirse(@RequestParam String plan, Authentication auth) {

        UsuarioAutenticado usuario = (UsuarioAutenticado) auth.getPrincipal();

        Suscripcion suscripcionGuardada = suscripcionService.crearSuscripcion(plan, usuario);
        return ResponseEntity.ok(suscripcionGuardada);
    }

}
