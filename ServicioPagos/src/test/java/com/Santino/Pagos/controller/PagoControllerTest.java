package com.Santino.Pagos.controller;

import com.Santino.Pagos.dto.PagoRequest;
import com.Santino.Pagos.service.PagoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("PagoController")
class PagoControllerTest {

    @Mock
    private PagoService pagoService;

    @InjectMocks
    private PagoController controller;

    @Test
    @DisplayName("POST /pagos/webhook: delega el cobro con la Idempotency-Key del header")
    void webhookUsaLaClaveDelHeader() {
        PagoRequest request = new PagoRequest(7L, "ana@mail.com", 50L, 1500L, "premium");

        ResponseEntity<Void> response = controller.webhook("evt-stripe-1", request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(pagoService).crearPago(request, "evt-stripe-1");
    }
}
