package com.Santino.Suscripciones.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("mapea NoSuchElementException a 404")
    void planInexistenteEsNotFound() {
        ResponseEntity<ErrorResponse> response = handler.handlePlanNoExiste(
                new NoSuchElementException("El plan solicitado no existe"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessage()).isEqualTo("El plan solicitado no existe");
    }

    @Test
    @DisplayName("mapea PlanAlreadyExists a 409")
    void planDuplicadoEsConflict() {
        ResponseEntity<ErrorResponse> response = handler.handlePlanExiste(
                new PlanAlreadyExists("Ya existe un plan con ese nombre"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Plan already exists");
    }

    @Test
    @DisplayName("mapea una excepción genérica a 500")
    void excepcionGenericaEsInternalServerError() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneralException(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
    }
}
