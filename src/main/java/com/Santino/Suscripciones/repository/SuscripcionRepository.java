package com.Santino.Suscripciones.repository;

import com.Santino.Suscripciones.entity.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SuscripcionRepository extends JpaRepository<Suscripcion, UUID> {
    
}
