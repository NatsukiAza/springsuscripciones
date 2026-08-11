package com.Santino.Suscripciones.controller;

import org.springframework.web.bind.annotation.RestController;

import com.Santino.Suscripciones.dto.PlanRequest;
import com.Santino.Suscripciones.service.PlanService;
import com.Santino.Suscripciones.entity.Plan;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


@RestController
public class PlanController {
    
    private final PlanService planService;

    public PlanController(PlanService planService){
        this.planService = planService;
    }

    @PostMapping("/plan/crear")
    public ResponseEntity<Plan> crearPlan(@RequestBody PlanRequest request) {

        final Plan planGuardado = planService.crearPlan(new Plan(request.nombre(), request.descripcion()));
        
        if(planGuardado == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El plan ya existe");
        }

        return ResponseEntity.ok(planGuardado);
    }
    

    @GetMapping("/plan")
    public boolean existPlan(@RequestParam String nombre) {
        return planService.existPlan(nombre);
    }
    

}
