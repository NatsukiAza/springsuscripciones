package com.Santino.Suscripciones.service;

import com.Santino.Suscripciones.dto.SuscripcionResponse;
import com.Santino.Suscripciones.dto.UsuarioAutenticado;
import com.Santino.Suscripciones.repository.SuscripcionRepository;
import com.Santino.Suscripciones.entity.Suscripcion;
import com.Santino.Suscripciones.publisher.SuscripcionEventPublisher;
import com.Santino.Suscripciones.dto.PagoResponse;
import com.Santino.Suscripciones.dto.PagoRequest;
import com.Santino.Suscripciones.entity.Plan;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class SuscripcionService {

    private final SuscripcionRepository suscripcionRepository;
    private final PlanService planService;
    private final SuscripcionEventPublisher eventPublisher;

    public SuscripcionService(SuscripcionRepository suscripcionRepository, PlanService planService,
            SuscripcionEventPublisher eventPublisher) {
        this.suscripcionRepository = suscripcionRepository;
        this.planService = planService;
        this.eventPublisher = eventPublisher;
    }

    public Suscripcion crearSuscripcion(String plan, UsuarioAutenticado usuario) {

        Optional<Plan> planDeseado = planService.mostrarPlan(plan);
        if (planDeseado.isEmpty())
            throw new NoSuchElementException("El plan solicitado no existe");

        final Suscripcion suscripcion = new Suscripcion(planDeseado.get().getID(), usuario.userId());
        final Suscripcion suscripcionGuardada = suscripcionRepository.save(suscripcion);
        eventPublisher
                .publicarSuscripcion(
                        new PagoRequest(usuario.userId(), usuario.email(), suscripcionGuardada.getID(),
                                planDeseado.get().getCosto(), planDeseado.get().getNombre()));
        return suscripcionGuardada;
    }

    public void handlePagoExitoso(PagoResponse response) {
        Optional<Suscripcion> suscripcionGuardada = suscripcionRepository.findById(response.suscripcionId());

        suscripcionGuardada.get().setEstado("Activo");

        suscripcionRepository.save(suscripcionGuardada.get());
    }

    public void handlePagoFallido(PagoResponse response) {
        Optional<Suscripcion> suscripcionGuardada = suscripcionRepository.findById(response.suscripcionId());

        suscripcionGuardada.get().setEstado("Pago rechazado");

        suscripcionRepository.save(suscripcionGuardada.get());
    }

    public SuscripcionResponse verPlanUsuario(UsuarioAutenticado usuario) {
        Optional<Suscripcion> suscripcionEncontrada = suscripcionRepository.findByUserID(usuario.userId());

        if (suscripcionEncontrada.isEmpty())
            throw new NoSuchElementException("No se encontro suscripcion para este usuario");

        return new SuscripcionResponse(suscripcionEncontrada.get().getID(), suscripcionEncontrada.get().getUserID(),
                suscripcionEncontrada.get().getPlanID(), suscripcionEncontrada.get().getEstado());
    }
}
