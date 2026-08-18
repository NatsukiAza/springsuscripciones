package com.Santino.Suscripciones.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.Santino.Suscripciones.entity.Plan;
import com.Santino.Suscripciones.repository.PlanRepository;
import com.Santino.Suscripciones.exception.PlanAlreadyExists;

import java.util.Optional;

@Service
public class PlanService {
    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @CacheEvict(value = "planes", allEntries = true)
    public Plan crearPlan(Plan plan) {
        if (planRepository.existsByNombre(plan.getNombre())) {
            throw new PlanAlreadyExists("Ya existe un plan con ese nombre");
        }

        return planRepository.save(plan);
    }

    @Cacheable(value = "planes", key = "#nombre")
    public Optional<Plan> mostrarPlan(String nombre) {
        return planRepository.findByNombre(nombre);
    }

    public boolean existPlan(String nombre) {
        return planRepository.existsByNombre(nombre);
    }

}
