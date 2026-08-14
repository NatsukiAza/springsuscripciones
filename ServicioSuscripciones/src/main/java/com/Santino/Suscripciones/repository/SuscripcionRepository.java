package com.Santino.Suscripciones.repository;

import com.Santino.Suscripciones.entity.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {

  public Optional<Suscripcion> findByUserID(Long userId);

}
