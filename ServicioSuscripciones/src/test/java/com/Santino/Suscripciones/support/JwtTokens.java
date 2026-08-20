package com.Santino.Suscripciones.support;

import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public final class JwtTokens {

    private static final String SECRET_KEY = "MICLAVESECRETASUPERSEGURAYMUYLARGAPARASPRINGSECURITY";

    private JwtTokens() {
    }

    public static String bearer(long userId, String email, String username) {
        String token = Jwts.builder()
                .subject(username)
                .claim("userID", userId)
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)))
                .compact();
        return "Bearer " + token;
    }
}
