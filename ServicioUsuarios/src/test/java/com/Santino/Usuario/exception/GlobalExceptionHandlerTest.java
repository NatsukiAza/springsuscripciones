package com.Santino.Usuario.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler (usuarios)")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("credenciales inválidas → 401")
    void contraseñaIncorrecta() {
        ResponseEntity<ErrorResponse> response = handler.contraseñaIncorrecta(
                new CredentialsException("La contraseña o el usuario ingresado es incorrecta"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(401);
        assertThat(response.getBody().getError()).isEqualTo("Credentials do not match");
        assertThat(response.getBody().getMessage()).isEqualTo("La contraseña o el usuario ingresado es incorrecta");
    }

    @Test
    @DisplayName("usuario duplicado → 409")
    void usuarioYaExiste() {
        ResponseEntity<ErrorResponse> response = handler.usuarioYaExiste(
                new UserAlreadyExists("El nombre de usuario ya existe"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getError()).isEqualTo("User already created");
    }

    @Test
    @DisplayName("recurso inexistente → 404")
    void usuarioNoEncontrado() {
        ResponseEntity<ErrorResponse> response = handler.usuarioNoEncontradoHandler(
                new NoSuchElementException("no está"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Element was not found");
    }

    @Test
    @DisplayName("error no contemplado → 500")
    void handlerGenerico() {
        ResponseEntity<ErrorResponse> response = handler.handlerGenerico(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Error interno del servidor");
        assertThat(response.getBody().getMessage()).isEqualTo("boom");
    }
}
