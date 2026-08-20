package com.Santino.Notificaciones.service;

import com.Santino.Notificaciones.dto.NotificacionRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NotificacionService")
class NotificacionServiceTest {

    private final NotificacionService service = new NotificacionService();
    private final PrintStream stdoutOriginal = System.out;
    private ByteArrayOutputStream captura;

    @BeforeEach
    void setUp() {
        captura = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captura));
    }

    @AfterEach
    void tearDown() {
        System.setOut(stdoutOriginal);
    }

    @Test
    @DisplayName("informa un pago realizado cuando el estado es Exitoso")
    void informaPagoRealizado() {
        service.enviarNotificacion(new NotificacionRequest(
                50L, 99L, "Exitoso", "ana@mail.com", "premium"));

        assertThat(captura.toString())
                .contains("ana@mail.com")
                .contains("realizado")
                .contains("premium")
                .doesNotContain("rechazado");
    }

    @Test
    @DisplayName("informa un pago rechazado cuando el estado no es Activo")
    void informaPagoRechazado() {
        service.enviarNotificacion(new NotificacionRequest(
                50L, 99L, "Fallido", "ana@mail.com", "premium"));

        assertThat(captura.toString())
                .contains("ana@mail.com")
                .contains("rechazado")
                .contains("premium")
                .doesNotContain("realizado");
    }
}
