package com.Santino.Usuario.security;

import com.Santino.Usuario.entity.Usuario;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("JwtService (usuarios)")
class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    @DisplayName("generarToken: firma un JWT con subject, userID y email")
    void generarTokenIncluyeClaims() {
        Usuario usuario = usuarioSanti();

        String token = jwtService.generarToken(usuario);

        assertThat(jwtService.extraerUsername(token)).isEqualTo("santi");
        assertThat(jwtService.esTokenValido(token, userDetails("santi"))).isTrue();

        var claims = Jwts.parser()
                .verifyWith(jwtService.getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        assertThat(((Number) claims.get("userID")).longValue()).isEqualTo(15L);
        assertThat(claims.get("email")).isEqualTo("santi@streamsub.com");
        assertThat(claims.getExpiration()).isAfter(new Date());
    }

    @Test
    @DisplayName("esTokenValido: false si el username del UserDetails no coincide")
    void tokenConOtroUsuario() {
        String token = jwtService.generarToken(usuarioSanti());

        assertThat(jwtService.esTokenValido(token, userDetails("otro"))).isFalse();
    }

    @Test
    @DisplayName("un token expirado no se puede validar")
    void tokenExpirado() {
        String token = Jwts.builder()
                .subject("santi")
                .issuedAt(new Date(System.currentTimeMillis() - 120_000))
                .expiration(new Date(System.currentTimeMillis() - 60_000))
                .signWith(jwtService.getSignKey())
                .compact();

        assertThatThrownBy(() -> jwtService.esTokenValido(token, userDetails("santi")))
                .isInstanceOf(ExpiredJwtException.class);
    }

    private Usuario usuarioSanti() {
        Usuario usuario = new Usuario("santi", "santi@streamsub.com", "hash");
        ReflectionTestUtils.setField(usuario, "ID", 15L);
        return usuario;
    }

    private UserDetails userDetails(String username) {
        UserDetails details = mock(UserDetails.class);
        when(details.getUsername()).thenReturn(username);
        return details;
    }
}
