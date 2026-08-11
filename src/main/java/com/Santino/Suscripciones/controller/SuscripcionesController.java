package com.Santino.Suscripciones.controller;

import com.Santino.Suscripciones.entity.Suscripcion;
import com.Santino.Suscripciones.service.SuscripcionService;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;


@RestController
public class SuscripcionesController {
    
    private SuscripcionService suscripcionService;

    @PostMapping("/suscribirse")
    public ResponseEntity<Suscripcion> suscribirse(@RequestParam Long plan) {
        
        Suscripcion suscripcionGuardada = suscripcionService.crearSuscripcion(plan);
        return ResponseEntity.ok(suscripcionGuardada);
    }
    

}
