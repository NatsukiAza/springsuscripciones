package com.Santino.Suscripciones.service;

import com.Santino.Suscripciones.repository.SuscripcionRepository;
import com.Santino.Suscripciones.entity.Suscripcion;

public class SuscripcionService {
    private SuscripcionRepository suscripcionRepository;

    public Suscripcion crearSuscripcion(Long plan){

        final Suscripcion suscripcion = new Suscripcion(plan);
        final Suscripcion suscripcionGuardada = suscripcionRepository.save(suscripcion);
        return suscripcionGuardada;
    }
}
