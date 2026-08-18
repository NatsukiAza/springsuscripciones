package com.Santino.Usuario.controller;

import com.Santino.Usuario.dto.AuthResponse;
import com.Santino.Usuario.dto.LoginRequest;
import com.Santino.Usuario.dto.UsuarioRequest;
import com.Santino.Usuario.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioController")
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController controller;

    @Test
    @DisplayName("POST /auth/registrarse: responde 200 con el usuario creado")
    void crearUsuario() {
        UsuarioRequest request = new UsuarioRequest("santi", "santi@streamsub.com", "clave");
        UsuarioRequest persistido = new UsuarioRequest("santi", "santi@streamsub.com", "hash");
        when(usuarioService.crearUsuario(request)).thenReturn(persistido);

        ResponseEntity<UsuarioRequest> response = controller.crearUsuario(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(persistido);
    }

    @Test
    @DisplayName("POST /auth/login: envuelve el JWT en AuthResponse")
    void inicioSesion() {
        LoginRequest request = new LoginRequest("santi", "clave");
        when(usuarioService.iniciarSesion(request)).thenReturn("jwt-firmado");

        ResponseEntity<AuthResponse> response = controller.inicioSesion(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(new AuthResponse("jwt-firmado"));
    }
}
