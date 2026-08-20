package com.Santino.Usuario.controller;

import com.Santino.Usuario.entity.Usuario;
import com.Santino.Usuario.repository.UsuarioRepository;
import com.Santino.Usuario.security.JwtService;
import com.Santino.Usuario.support.IntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@DisplayName("Auth API (integración)")
class UsuarioAuthIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void limpiar() {
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /auth/registrarse persiste el usuario con password hasheada")
    void registrarsePersisteConHash() throws Exception {
        mockMvc.perform(post("/auth/registrarse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"santi","email":"santi@streamsub.com","password":"clave-plana"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("santi"))
                .andExpect(jsonPath("$.email").value("santi@streamsub.com"))
                .andExpect(jsonPath("$.password").value(not("clave-plana")));

        Usuario persistido = usuarioRepository.findByUsername("santi").orElseThrow();
        assertThat(persistido.getEmail()).isEqualTo("santi@streamsub.com");
        assertThat(persistido.getPassword()).isNotEqualTo("clave-plana");
        assertThat(persistido.getPassword()).startsWith("$2");
    }

    @Test
    @DisplayName("POST /auth/registrarse: username duplicado responde 409")
    void registrarseDuplicado() throws Exception {
        mockMvc.perform(post("/auth/registrarse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"santi","email":"santi@streamsub.com","password":"clave-plana"}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/registrarse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"santi","email":"otro@mail.com","password":"otra-clave"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("User already created"));
    }

    @Test
    @DisplayName("POST /auth/login emite un JWT válido")
    void loginEmiteJwt() throws Exception {
        mockMvc.perform(post("/auth/registrarse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"santi","email":"santi@streamsub.com","password":"clave-plana"}
                                """))
                .andExpect(status().isOk());

        String body = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"santi","password":"clave-plana"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = JsonPath.read(body, "$.token");
        assertThat(jwtService.extraerUsername(token)).isEqualTo("santi");
    }

    @Test
    @DisplayName("POST /auth/login con credenciales inválidas responde 401")
    void loginInvalido() throws Exception {
        mockMvc.perform(post("/auth/registrarse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"santi","email":"santi@streamsub.com","password":"clave-plana"}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"santi","password":"otra-clave"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credentials do not match"));
    }
}
