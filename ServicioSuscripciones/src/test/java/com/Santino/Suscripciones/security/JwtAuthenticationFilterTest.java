package com.Santino.Suscripciones.security;

import com.Santino.Suscripciones.dto.UsuarioAutenticado;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("deja pasar la petición si no hay Bearer token")
    void continuaSinAutenticarSiNoHayBearer() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("coloca UsuarioAutenticado en el SecurityContext cuando el JWT es válido")
    void autenticaConUsuarioAutenticado() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer jwt.valido");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extraerUsername("jwt.valido")).thenReturn("ana");
        when(jwtService.extraerUserID("jwt.valido")).thenReturn(7L);
        when(jwtService.extraerEmail("jwt.valido")).thenReturn("ana@mail.com");
        when(jwtService.esTokenValido("jwt.valido")).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo(new UsuarioAutenticado(7L, "ana@mail.com"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("continúa la cadena aunque el token sea inválido")
    void continuaSiElTokenEsInvalido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer jwt.roto");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extraerUsername("jwt.roto")).thenThrow(new RuntimeException("token inválido"));

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}
