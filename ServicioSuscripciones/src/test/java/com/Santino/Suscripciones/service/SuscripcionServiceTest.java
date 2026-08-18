package com.Santino.Suscripciones.service;

import com.Santino.Suscripciones.dto.PagoRequest;
import com.Santino.Suscripciones.dto.PagoResponse;
import com.Santino.Suscripciones.dto.SuscripcionResponse;
import com.Santino.Suscripciones.dto.UsuarioAutenticado;
import com.Santino.Suscripciones.entity.Plan;
import com.Santino.Suscripciones.entity.Suscripcion;
import com.Santino.Suscripciones.publisher.SuscripcionEventPublisher;
import com.Santino.Suscripciones.repository.SuscripcionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SuscripcionService")
class SuscripcionServiceTest {

    @Mock
    private SuscripcionRepository suscripcionRepository;

    @Mock
    private PlanService planService;

    @Mock
    private SuscripcionEventPublisher eventPublisher;

    @InjectMocks
    private SuscripcionService suscripcionService;

    private final UsuarioAutenticado usuario = new UsuarioAutenticado(7L, "ana@mail.com");

    @Nested
    @DisplayName("crearSuscripcion")
    class CrearSuscripcion {

        @Test
        @DisplayName("persiste en estado Pendiente y publica el evento de cobro")
        void persisteYPublicaEventoDeCobro() {
            Plan plan = new Plan("premium", "Full HD", 1500L);
            ReflectionTestUtils.setField(plan, "ID", 10L);
            when(planService.mostrarPlan("premium")).thenReturn(Optional.of(plan));
            when(suscripcionRepository.save(any(Suscripcion.class))).thenAnswer(invocation -> {
                Suscripcion suscripcion = invocation.getArgument(0);
                ReflectionTestUtils.setField(suscripcion, "ID", 50L);
                return suscripcion;
            });

            Suscripcion resultado = suscripcionService.crearSuscripcion("premium", usuario);

            assertThat(resultado.getID()).isEqualTo(50L);
            assertThat(resultado.getPlanID()).isEqualTo(10L);
            assertThat(resultado.getUserID()).isEqualTo(7L);
            assertThat(resultado.getEstado()).isEqualTo("Pendiente");

            ArgumentCaptor<PagoRequest> captor = ArgumentCaptor.forClass(PagoRequest.class);
            verify(eventPublisher).publicarSuscripcion(captor.capture());
            assertThat(captor.getValue()).isEqualTo(new PagoRequest(7L, "ana@mail.com", 50L, 1500L, "premium"));
        }

        @Test
        @DisplayName("lanza NoSuchElementException si el plan no existe y no publica nada")
        void lanzaExcepcionSiElPlanNoExiste() {
            when(planService.mostrarPlan("inexistente")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> suscripcionService.crearSuscripcion("inexistente", usuario))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("El plan solicitado no existe");

            verify(suscripcionRepository, never()).save(any());
            verifyNoInteractions(eventPublisher);
        }
    }

    @Nested
    @DisplayName("callbacks de pago")
    class CallbacksDePago {

        @Test
        @DisplayName("handlePagoExitoso deja la suscripción en Activo")
        void pagoExitosoActivaLaSuscripcion() {
            Suscripcion suscripcion = new Suscripcion(10L, 7L);
            ReflectionTestUtils.setField(suscripcion, "ID", 50L);
            when(suscripcionRepository.findById(50L)).thenReturn(Optional.of(suscripcion));

            suscripcionService.handlePagoExitoso(new PagoResponse(50L, 99L, "Exitoso", "ana@mail.com", 10L));

            ArgumentCaptor<Suscripcion> captor = ArgumentCaptor.forClass(Suscripcion.class);
            verify(suscripcionRepository).save(captor.capture());
            assertThat(captor.getValue().getEstado()).isEqualTo("Activo");
        }

        @Test
        @DisplayName("handlePagoFallido deja la suscripción en Pago rechazado")
        void pagoFallidoRechazaLaSuscripcion() {
            Suscripcion suscripcion = new Suscripcion(10L, 7L);
            ReflectionTestUtils.setField(suscripcion, "ID", 50L);
            when(suscripcionRepository.findById(50L)).thenReturn(Optional.of(suscripcion));

            suscripcionService.handlePagoFallido(new PagoResponse(50L, 99L, "Fallido", "ana@mail.com", 10L));

            ArgumentCaptor<Suscripcion> captor = ArgumentCaptor.forClass(Suscripcion.class);
            verify(suscripcionRepository).save(captor.capture());
            assertThat(captor.getValue().getEstado()).isEqualTo("Pago rechazado");
        }

        @Test
        @DisplayName("handlePagoExitoso falla si la suscripción no existe")
        void pagoExitosoFallaSiNoExisteLaSuscripcion() {
            when(suscripcionRepository.findById(50L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> suscripcionService.handlePagoExitoso(
                    new PagoResponse(50L, 99L, "Exitoso", "ana@mail.com", 10L)))
                    .isInstanceOf(NoSuchElementException.class);

            verify(suscripcionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("verPlanUsuario")
    class VerPlanUsuario {

        @Test
        @DisplayName("devuelve la suscripción del usuario autenticado")
        void devuelveLaSuscripcionDelUsuario() {
            Suscripcion suscripcion = new Suscripcion(10L, 7L);
            ReflectionTestUtils.setField(suscripcion, "ID", 50L);
            suscripcion.setEstado("Activo");
            when(suscripcionRepository.findByUserID(7L)).thenReturn(Optional.of(suscripcion));

            SuscripcionResponse response = suscripcionService.verPlanUsuario(usuario);

            assertThat(response).isEqualTo(new SuscripcionResponse(50L, 7L, 10L, "Activo"));
        }

        @Test
        @DisplayName("lanza NoSuchElementException si el usuario no tiene suscripción")
        void lanzaExcepcionSiNoHaySuscripcion() {
            when(suscripcionRepository.findByUserID(7L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> suscripcionService.verPlanUsuario(usuario))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("No se encontro suscripcion para este usuario");
        }
    }
}
