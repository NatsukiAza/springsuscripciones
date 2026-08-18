package com.Santino.Usuario;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Smoke test de contexto: requiere infra local. El dominio se cubre con tests unitarios.")
@SpringBootTest
class UsuarioApplicationTests {

    @Test
    void contextLoads() {
    }
}
