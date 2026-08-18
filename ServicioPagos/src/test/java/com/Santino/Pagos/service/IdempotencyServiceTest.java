package com.Santino.Pagos.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("IdempotencyService")
class IdempotencyServiceTest {

    @Mock
    private StringRedisTemplate redis;

    @Mock
    private ValueOperations<String, String> values;

    @InjectMocks
    private IdempotencyService idempotencyService;

    @Test
    @DisplayName("esPrimeraVez: true solo si Redis acepta el SET NX")
    void primeraVezCuandoLaClaveEsNueva() {
        when(redis.opsForValue()).thenReturn(values);
        when(values.setIfAbsent(eq("idempotency:evt-1"), eq("processed"), eq(Duration.ofHours(24))))
                .thenReturn(true);

        assertThat(idempotencyService.esPrimeraVez("evt-1")).isTrue();
    }

    @Test
    @DisplayName("esPrimeraVez: false si la clave ya existía")
    void duplicadoCuandoLaClaveYaExiste() {
        when(redis.opsForValue()).thenReturn(values);
        when(values.setIfAbsent(eq("idempotency:evt-1"), eq("processed"), eq(Duration.ofHours(24))))
                .thenReturn(false);

        assertThat(idempotencyService.esPrimeraVez("evt-1")).isFalse();
    }

    @Test
    @DisplayName("liberar borra la clave para permitir un retry")
    void liberarBorraLaClave() {
        idempotencyService.liberar("evt-1");

        verify(redis).delete("idempotency:evt-1");
    }
}
