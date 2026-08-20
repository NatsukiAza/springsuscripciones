package com.Santino.Suscripciones.controller;

import org.springframework.web.bind.annotation.RestController;

import com.Santino.Suscripciones.dto.PlanRequest;
import com.Santino.Suscripciones.exception.ErrorResponse;
import com.Santino.Suscripciones.service.PlanService;
import com.Santino.Suscripciones.entity.Plan;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Planes", description = "Alta y consulta de planes. El GET se cachea en Redis.")
@SecurityRequirement(name = "bearer-jwt")
@RestController
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @Operation(summary = "Crear plan")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plan persistido"),
            @ApiResponse(responseCode = "409", description = "Ya existe un plan con ese nombre",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/plan/crear")
    public ResponseEntity<Plan> crearPlan(@RequestBody PlanRequest request) {

        final Plan planGuardado = planService
                .crearPlan(new Plan(request.nombre(), request.descripcion(), request.costo()));
        return ResponseEntity.ok(planGuardado);
    }

    @Operation(summary = "¿Existe el plan?", description = "Consulta por nombre. Pensado para validar antes de suscribirse.")
    @GetMapping("/plan")
    public boolean existPlan(
            @Parameter(description = "Nombre del plan", example = "premium")
            @RequestParam String nombre) {
        return planService.existPlan(nombre);
    }

}
