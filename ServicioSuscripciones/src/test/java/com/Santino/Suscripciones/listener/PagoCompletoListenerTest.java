package com.Santino.Suscripciones.listener;

import com.Santino.Suscripciones.dto.PagoResponse;
import com.Santino.Suscripciones.service.SuscripcionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("PagoCompletoListener")
class PagoCompletoListenerTest {

    @Mock
    private SuscripcionService suscripcionService;

    @InjectMocks
    private PagoCompletoListener listener;

    @Test
    @DisplayName("ruta el pago exitoso al handler correspondiente")
    void delegaPagoExitoso() {
        PagoResponse response = new PagoResponse(50L, 99L, "Exitoso", "ana@mail.com", 10L);

        listener.recibirPagoExitoso(response);

        verify(suscripcionService).handlePagoExitoso(response);
    }

    @Test
    @DisplayName("ruta el pago fallido al handler correspondiente")
    void delegaPagoFallido() {
        PagoResponse response = new PagoResponse(50L, 99L, "Fallido", "ana@mail.com", 10L);

        listener.recibirPagoFallido(response);

        verify(suscripcionService).handlePagoFallido(response);
    }
}
