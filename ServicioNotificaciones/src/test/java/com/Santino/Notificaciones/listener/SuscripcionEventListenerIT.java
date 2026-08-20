package com.Santino.Notificaciones.listener;

import com.Santino.Notificaciones.config.RabbitConfig;
import com.Santino.Notificaciones.dto.NotificacionRequest;
import com.Santino.Notificaciones.service.NotificacionService;
import com.Santino.Notificaciones.support.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@IntegrationTest
@DisplayName("SuscripcionEventListener (integración)")
class SuscripcionEventListenerIT {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitConfig rabbitConfig;

    @Autowired
    private NotificacionService notificacionService;

    @Test
    @DisplayName("un pago exitoso dispara enviarNotificacion")
    void consumeElEventoYNotifica() {
        NotificacionRequest request = new NotificacionRequest(50L, 99L, "Exitoso", "ana@mail.com", "premium");

        rabbitTemplate.convertAndSend(
                rabbitConfig.pagosExchangeName,
                rabbitConfig.pagoExitosoRoutingKey,
                request);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(
                () -> verify(notificacionService).enviarNotificacion(request));
    }
}
