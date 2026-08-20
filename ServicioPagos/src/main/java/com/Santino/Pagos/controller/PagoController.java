package com.Santino.Pagos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.Santino.Pagos.dto.PagoRequest;
import com.Santino.Pagos.service.PagoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Webhook", description = "Contrato del PSP simulado. El flujo feliz de cobro entra por Rabbit, no por acá.")
@RestController
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @Operation(
            summary = "Webhook de cobro",
            description = "Público a propósito (lo llamaría Stripe/MP). La misma `Idempotency-Key` no cobra dos veces.")
    @ApiResponse(responseCode = "200", description = "Pago aceptado o replay idempotente")
    @PostMapping("/pagos/webhook")
    public ResponseEntity<Void> webhook(
            @Parameter(
                    description = "Clave del evento del PSP. Redis SET NX, TTL 24h. El listener de Rabbit usa `suscripcion:{id}`.",
                    example = "evt_1N2abc")
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody PagoRequest request) {
        pagoService.crearPago(request, idempotencyKey);
        return ResponseEntity.ok().build();
    }
}
