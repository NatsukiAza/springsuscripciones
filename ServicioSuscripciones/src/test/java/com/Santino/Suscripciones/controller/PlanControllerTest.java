package com.Santino.Suscripciones.controller;

import com.Santino.Suscripciones.dto.PlanRequest;
import com.Santino.Suscripciones.entity.Plan;
import com.Santino.Suscripciones.service.PlanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlanController")
class PlanControllerTest {

    @Mock
    private PlanService planService;

    @InjectMocks
    private PlanController controller;

    @Test
    @DisplayName("POST /plan/crear mapea el request a la entidad Plan")
    void crearPlanMapeaElRequest() {
        PlanRequest request = new PlanRequest("premium", "Full HD", 1500L);
        when(planService.crearPlan(any(Plan.class))).thenAnswer(invocation -> {
            Plan plan = invocation.getArgument(0);
            ReflectionTestUtils.setField(plan, "ID", 1L);
            return plan;
        });

        ResponseEntity<Plan> response = controller.crearPlan(request);

        ArgumentCaptor<Plan> captor = ArgumentCaptor.forClass(Plan.class);
        verify(planService).crearPlan(captor.capture());
        assertThat(captor.getValue().getNombre()).isEqualTo("premium");
        assertThat(captor.getValue().getDescripcion()).isEqualTo("Full HD");
        assertThat(captor.getValue().getCosto()).isEqualTo(1500L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getID()).isEqualTo(1L);
    }

    @Test
    @DisplayName("GET /plan consulta existencia por nombre")
    void existPlanDelegaEnElServicio() {
        when(planService.existPlan("premium")).thenReturn(true);

        assertThat(controller.existPlan("premium")).isTrue();
        verify(planService).existPlan("premium");
    }
}
