package com.Santino.Pagos.publisher;

import com.Santino.Pagos.config.RabbitConfig;
import com.Santino.Pagos.dto.PagoRequest;
import com.Santino.Pagos.dto.SuscripcionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("PagoEventPublisher")
class PagoEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private PagoEventPublisher publisher;
    private final PagoRequest request = new PagoRequest(7L, "ana@mail.com", 50L, 1500L, "premium");

    @BeforeEach
    void setUp() {
        RabbitConfig rabbitConfig = new RabbitConfig();
        rabbitConfig.pagosExchangeName = "pagos-exchange";
        rabbitConfig.pagoExitosoRoutingKey = "pago-exitoso";
        rabbitConfig.pagoFallidoRoutingKey = "pago-fallido";
        publisher = new PagoEventPublisher(rabbitTemplate, rabbitConfig);
    }

    @Test
    @DisplayName("publica pago.exitoso con estado Exitoso")
    void publicaPagoExitoso() {
        publisher.PagoSuccessPublisher(request, 99L);

        verify(rabbitTemplate).convertAndSend(
                "pagos-exchange",
                "pago-exitoso",
                new SuscripcionResponse(50L, 99L, "Exitoso", "ana@mail.com", "premium"));
    }

    @Test
    @DisplayName("publica pago.fallido con estado Fallido")
    void publicaPagoFallido() {
        publisher.PagoFailPublisher(request, 99L);

        verify(rabbitTemplate).convertAndSend(
                "pagos-exchange",
                "pago-fallido",
                new SuscripcionResponse(50L, 99L, "Fallido", "ana@mail.com", "premium"));
    }
}
