package com.Santino.Usuario.security;

import com.Santino.Usuario.entity.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

public class JwtService {

    private static final String SECRET_KEY = "MI_CLAVE_SECRETA_SUPER_SEGURA_Y_MUY_LARGA_PARA_SPRING_SECURITY";

    public String generarToken(Usuario usuario){
        return Jwts.builder()
        .subject(usuario.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
        .signWith(getSignKey())
        .compact();  
    }

    public String extraerUsername(String token){
        return extraerClaim(token, Claims::getSubject);
    }

    public boolean esTokenValido(String token, Usuario usuario){
        final String username = extraerUsername(token);
        return (username.equals(usuario.getUsername()) && !esTokenExpirado(token));
    }

    private boolean esTokenExpirado(String token){
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extraerClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = Jwts.parser()
        .verifyWith(getSignKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
        return claimsResolver.apply(claims);
    }

    public SecretKey getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
