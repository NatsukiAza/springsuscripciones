package com.Santino.Pagos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.Santino.Pagos.dto.PagoRequest;
import com.Santino.Pagos.service.PagoService;

@RestController
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/pagos/webhook")
    public ResponseEntity<Void> webhook(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody PagoRequest request) {
        pagoService.crearPago(request, idempotencyKey);
        return ResponseEntity.ok().build();
    }
}
