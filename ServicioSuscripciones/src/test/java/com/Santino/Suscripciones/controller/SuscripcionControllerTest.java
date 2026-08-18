package com.Santino.Suscripciones.controller;

import com.Santino.Suscripciones.dto.SuscripcionResponse;
import com.Santino.Suscripciones.dto.UsuarioAutenticado;
import com.Santino.Suscripciones.entity.Suscripcion;
import com.Santino.Suscripciones.service.SuscripcionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SuscripcionController")
class SuscripcionControllerTest {

    @Mock
    private SuscripcionService suscripcionService;

    @InjectMocks
    private SuscripcionController controller;

    @Test
    @DisplayName("GET /suscripcion usa el principal autenticado")
    void verSuscripcionUsaElUsuarioAutenticado() {
        UsuarioAutenticado usuario = new UsuarioAutenticado(7L, "ana@mail.com");
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(usuario);
        SuscripcionResponse esperado = new SuscripcionResponse(50L, 7L, 10L, "Activo");
        when(suscripcionService.verPlanUsuario(usuario)).thenReturn(esperado);

        ResponseEntity<SuscripcionResponse> response = controller.verSuscripcion(auth);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(esperado);
        verify(suscripcionService).verPlanUsuario(usuario);
    }

    @Test
    @DisplayName("POST /suscribirse crea la suscripción del plan pedido")
    void suscribirseDelegaEnElServicio() {
        UsuarioAutenticado usuario = new UsuarioAutenticado(7L, "ana@mail.com");
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(usuario);
        Suscripcion guardada = new Suscripcion(10L, 7L);
        ReflectionTestUtils.setField(guardada, "ID", 50L);
        when(suscripcionService.crearSuscripcion("premium", usuario)).thenReturn(guardada);

        ResponseEntity<Suscripcion> response = controller.suscribirse("premium", auth);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(guardada);
    }
}
