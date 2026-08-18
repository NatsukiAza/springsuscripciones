package com.Santino.Notificaciones.listener;

import com.Santino.Notificaciones.dto.NotificacionRequest;
import com.Santino.Notificaciones.service.NotificacionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("SuscripcionEventListener")
class SuscripcionEventListenerTest {

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private SuscripcionEventListener listener;

    @Test
    @DisplayName("delega el mensaje de RabbitMQ en NotificacionService")
    void delegaEnElServicio() {
        NotificacionRequest request = new NotificacionRequest(50L, 99L, "Activo", "ana@mail.com", "premium");

        listener.recibirSuscripcion(request);

        verify(notificacionService).enviarNotificacion(request);
    }
}
