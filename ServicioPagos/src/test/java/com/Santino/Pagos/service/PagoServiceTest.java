package com.Santino.Pagos.service;

import com.Santino.Pagos.dto.PagoRequest;
import com.Santino.Pagos.entity.Pago;
import com.Santino.Pagos.publisher.PagoEventPublisher;
import com.Santino.Pagos.repository.PagoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PagoService")
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PagoEventPublisher pagoEventPublisher;

    @Mock
    private IdempotencyService idempotencyService;

    @InjectMocks
    private PagoService pagoService;

    private final PagoRequest request = new PagoRequest(7L, "ana@mail.com", 50L, 1500L, "premium");

    @Nested
    @DisplayName("crearPago")
    class CrearPago {

        @Test
        @DisplayName("persiste SUCCESS y publica pago exitoso cuando el cobro simulado aprueba")
        void publicaEventoDeExitoCuandoElCobroAprueba() {
            when(idempotencyService.esPrimeraVez("suscripcion:50")).thenReturn(true);
            stubSaveConId(99L);

            try (MockedConstruction<Random> ignored = mockConstruction(Random.class,
                    (mock, context) -> when(mock.nextInt(10)).thenReturn(0))) {
                pagoService.crearPago(request);
            }

            ArgumentCaptor<Pago> captor = ArgumentCaptor.forClass(Pago.class);
            verify(pagoRepository).save(captor.capture());
            assertThat(captor.getValue().getEstado()).isEqualTo("SUCCESS");
            assertThat(captor.getValue().getUserID()).isEqualTo(7L);
            assertThat(captor.getValue().getSuscripcionID()).isEqualTo(50L);
            assertThat(captor.getValue().getMonto()).isEqualTo(1500L);
            verify(pagoEventPublisher).PagoSuccessPublisher(request, 99L);
            verify(pagoEventPublisher, never()).PagoFailPublisher(any(), any());
        }

        @Test
        @DisplayName("persiste FAILURE y publica pago fallido cuando el cobro simulado rechaza")
        void publicaEventoDeFalloCuandoElCobroRechaza() {
            when(idempotencyService.esPrimeraVez("suscripcion:50")).thenReturn(true);
            stubSaveConId(99L);

            try (MockedConstruction<Random> ignored = mockConstruction(Random.class,
                    (mock, context) -> when(mock.nextInt(10)).thenReturn(9))) {
                pagoService.crearPago(request);
            }

            ArgumentCaptor<Pago> captor = ArgumentCaptor.forClass(Pago.class);
            verify(pagoRepository).save(captor.capture());
            assertThat(captor.getValue().getEstado()).isEqualTo("FAILURE");
            verify(pagoEventPublisher).PagoFailPublisher(request, 99L);
            verify(pagoEventPublisher, never()).PagoSuccessPublisher(any(), any());
        }

        @Test
        @DisplayName("no cobra de nuevo si la misma clave de idempotencia ya se procesó")
        void ignoraUnCobroDuplicado() {
            when(idempotencyService.esPrimeraVez("evt-stripe-1")).thenReturn(false);

            pagoService.crearPago(request, "evt-stripe-1");

            verify(pagoRepository, never()).save(any());
            verify(pagoEventPublisher, never()).PagoSuccessPublisher(any(), any());
            verify(pagoEventPublisher, never()).PagoFailPublisher(any(), any());
        }

        @Test
        @DisplayName("libera la clave si falla la persistencia para permitir un retry")
        void envuelveErrorDePersistenciaYLiberaLaClave() {
            when(idempotencyService.esPrimeraVez("suscripcion:50")).thenReturn(true);
            when(pagoRepository.save(any(Pago.class))).thenThrow(new RuntimeException("DB down"));

            try (MockedConstruction<Random> ignored = mockConstruction(Random.class,
                    (mock, context) -> when(mock.nextInt(10)).thenReturn(0))) {
                assertThatThrownBy(() -> pagoService.crearPago(request))
                        .isInstanceOf(RuntimeException.class)
                        .hasMessage("DB down");
            }

            verify(idempotencyService).liberar("suscripcion:50");
            verify(pagoEventPublisher, never()).PagoSuccessPublisher(any(), any());
            verify(pagoEventPublisher, never()).PagoFailPublisher(any(), any());
        }
    }

    private void stubSaveConId(Long id) {
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> {
            Pago pago = invocation.getArgument(0);
            ReflectionTestUtils.setField(pago, "ID", id);
            return pago;
        });
    }
}
