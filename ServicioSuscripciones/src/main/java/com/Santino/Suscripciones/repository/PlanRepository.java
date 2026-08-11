package com.Santino.Suscripciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Santino.Suscripciones.entity.Plan;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long>{

    public boolean existsByNombre(String nombre);

    public Optional<Plan> findByNombre(String nombre);
}
