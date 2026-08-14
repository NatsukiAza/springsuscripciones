package com.Santino.Pagos.security;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  public JwtAuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwt;

    // 1. Si no hay header o no empieza con "Bearer ", ignoramos y dejamos pasar la
    // petición
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    // 2. Extraer el token (después de "Bearer ")
    jwt = authHeader.substring(7);

    // 3. Si hay usuario y NO está autenticado en el contexto actual de Spring
    try {
      final String username = jwtService.extraerUsername(jwt);
      final Long userId = jwtService.extraerUserID(jwt);

      if (username != null && userId != null && SecurityContextHolder.getContext().getAuthentication() == null
          && jwtService.esTokenValido(jwt)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userId,
            null,
            List.of());

        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    } catch (Exception e) {

    }

    // 5. Continuar con el resto de filtros
    filterChain.doFilter(request, response);
  }
}