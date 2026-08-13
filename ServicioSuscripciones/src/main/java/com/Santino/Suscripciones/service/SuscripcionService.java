package com.Santino.Suscripciones.service;

import com.Santino.Suscripciones.service.PlanService;
import com.Santino.Suscripciones.repository.SuscripcionRepository;
import com.Santino.Suscripciones.entity.Suscripcion;
import com.Santino.Suscripciones.entity.Plan;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class SuscripcionService {

    private final SuscripcionRepository suscripcionRepository;
    private final PlanService planService;

    public SuscripcionService(SuscripcionRepository suscripcionRepository, PlanService planService) {
        this.suscripcionRepository = suscripcionRepository;
        this.planService = planService;
    }

    public final Suscripcion crearSuscripcion(String plan, Long userId) {

        Optional<Plan> planDeseado = planService.mostrarPlan(plan);
        if (planDeseado.isEmpty())
            throw new NoSuchElementException("El plan solicitado no existe");

        final Suscripcion suscripcion = new Suscripcion(planDeseado.get().getID(), userId);
        final Suscripcion suscripcionGuardada = suscripcionRepository.save(suscripcion);
        return suscripcionGuardada;
    }
}
