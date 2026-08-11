package com.Santino.Suscripciones.service;

import com.Santino.Suscripciones.repository.SuscripcionRepository;

import org.springframework.stereotype.Service;

import com.Santino.Suscripciones.entity.Suscripcion;

@Service
public class SuscripcionService {
    private SuscripcionRepository suscripcionRepository;

    public SuscripcionService(SuscripcionRepository suscripcionRepository){
        this.suscripcionRepository = suscripcionRepository;
    }

    public final Suscripcion crearSuscripcion(Long plan){

        final Suscripcion suscripcion = new Suscripcion(plan);
        final Suscripcion suscripcionGuardada = suscripcionRepository.save(suscripcion);
        return suscripcionGuardada;
    }
}
