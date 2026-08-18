package com.Santino.Suscripciones.service;

import com.Santino.Suscripciones.entity.Plan;
import com.Santino.Suscripciones.exception.PlanAlreadyExists;
import com.Santino.Suscripciones.repository.PlanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlanService")
class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @InjectMocks
    private PlanService planService;

    @Nested
    @DisplayName("crearPlan")
    class CrearPlan {

        @Test
        @DisplayName("guarda el plan cuando el nombre está libre")
        void guardaElPlanSiElNombreEstaLibre() {
            Plan plan = new Plan("premium", "Full HD", 1500L);
            when(planRepository.existsByNombre("premium")).thenReturn(false);
            when(planRepository.save(plan)).thenAnswer(invocation -> {
                Plan guardado = invocation.getArgument(0);
                ReflectionTestUtils.setField(guardado, "ID", 1L);
                return guardado;
            });

            Plan resultado = planService.crearPlan(plan);

            assertThat(resultado.getNombre()).isEqualTo("premium");
            assertThat(resultado.getID()).isEqualTo(1L);
            verify(planRepository).save(plan);
        }

        @Test
        @DisplayName("lanza PlanAlreadyExists si el nombre ya existe")
        void lanzaExcepcionSiElNombreYaExiste() {
            Plan plan = new Plan("premium", "Full HD", 1500L);
            when(planRepository.existsByNombre("premium")).thenReturn(true);

            assertThatThrownBy(() -> planService.crearPlan(plan))
                    .isInstanceOf(PlanAlreadyExists.class)
                    .hasMessage("Ya existe un plan con ese nombre");

            verify(planRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("consultar plan")
    class ConsultarPlan {

        @Test
        @DisplayName("mostrarPlan delega en el repositorio")
        void mostrarPlanDelegaEnElRepositorio() {
            Plan plan = new Plan("basic", "SD", 500L);
            when(planRepository.findByNombre("basic")).thenReturn(Optional.of(plan));

            assertThat(planService.mostrarPlan("basic")).contains(plan);
        }

        @Test
        @DisplayName("existPlan refleja el resultado del repositorio")
        void existPlanReflejaElRepositorio() {
            when(planRepository.existsByNombre("premium")).thenReturn(true);
            when(planRepository.existsByNombre("gold")).thenReturn(false);

            assertThat(planService.existPlan("premium")).isTrue();
            assertThat(planService.existPlan("gold")).isFalse();
        }
    }
}
