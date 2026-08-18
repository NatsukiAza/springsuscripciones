package com.Santino.Pagos.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtService")
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    @DisplayName("extrae username y userID de un token vigente")
    void extraeClaimsDeUnTokenVigente() {
        String token = tokenDe("ana", 7L, Instant.now().plus(1, ChronoUnit.HOURS));

        assertThat(jwtService.extraerUsername(token)).isEqualTo("ana");
        assertThat(jwtService.extraerUserID(token)).isEqualTo(7L);
        assertThat(jwtService.esTokenValido(token)).isTrue();
    }

    @Test
    @DisplayName("lanza ExpiredJwtException al validar un token vencido")
    void tokenExpiradoLanzaExcepcion() {
        String token = tokenDe("ana", 7L, Instant.now().minus(1, ChronoUnit.MINUTES));

        assertThatThrownBy(() -> jwtService.esTokenValido(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    private String tokenDe(String username, Long userId, Instant expiration) {
        return Jwts.builder()
                .subject(username)
                .claim("userID", userId)
                .issuedAt(Date.from(Instant.now().minus(1, ChronoUnit.HOURS)))
                .expiration(Date.from(expiration))
                .signWith(jwtService.getSignKey())
                .compact();
    }
}
