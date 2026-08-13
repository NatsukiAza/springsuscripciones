package com.Santino.Suscripciones.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String SECRET_KEY = "MICLAVESECRETASUPERSEGURAYMUYLARGAPARASPRINGSECURITY";

    public String extraerUsername(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public Long extraerUserID(String token) {
        return extraerClaim(token, claims -> {
            Object valor = claims.get("userID");
            if (valor instanceof Number numero)
                return numero.longValue();
            return null;
        });
    }

    public boolean esTokenValido(String token) {
        return !esTokenExpirado(token);
    }

    private boolean esTokenExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    public SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
