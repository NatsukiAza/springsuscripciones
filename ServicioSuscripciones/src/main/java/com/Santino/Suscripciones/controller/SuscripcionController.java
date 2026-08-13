package com.Santino.Suscripciones.controller;

import com.Santino.Suscripciones.entity.Suscripcion;
import com.Santino.Suscripciones.service.SuscripcionService;

import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping("/suscribirse")
    public ResponseEntity<Suscripcion> suscribirse(@RequestParam String plan, Authentication auth) {

        Long userId = (Long) auth.getPrincipal();

        Suscripcion suscripcionGuardada = suscripcionService.crearSuscripcion(plan, userId);
        return ResponseEntity.ok(suscripcionGuardada);
    }

}
