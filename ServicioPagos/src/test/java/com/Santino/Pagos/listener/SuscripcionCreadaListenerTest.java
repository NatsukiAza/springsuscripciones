package com.Santino.Pagos.listener;

import com.Santino.Pagos.dto.PagoRequest;
import com.Santino.Pagos.service.PagoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("SuscripcionCreadaListener")
class SuscripcionCreadaListenerTest {

    @Mock
    private PagoService pagoService;

    @InjectMocks
    private SuscripcionCreadaListener listener;

    @Test
    @DisplayName("dispara el cobro al recibir suscripcion.creada")
    void disparaElCobro() {
        PagoRequest request = new PagoRequest(7L, "ana@mail.com", 50L, 1500L, "premium");

        listener.recibirSuscripcion(request);

        verify(pagoService).crearPago(request);
    }
}
