package com.Santino.Usuario.security;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;
import java.io.IOException;
import org.springframework.security.core.userdetails.UserDetails;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  @Lazy
  private final UserDetailsService userDetailsService;

  public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String username;

    // 1. Si no hay header o no empieza con "Bearer ", ignoramos y dejamos pasar la
    // petición
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    // 2. Extraer el token (después de "Bearer ")
    jwt = authHeader.substring(7);
    username = jwtService.extraerUsername(jwt);

    // 3. Si hay usuario y NO está autenticado en el contexto actual de Spring
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

      // 4. Si el token es válido, autenticamos manualmente al usuario en el
      // SecurityContext
      if (jwtService.esTokenValido(jwt, userDetails)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // ¡AQUÍ ES DONDE SPRING ENTERA DE QUE EL USUARIO TIENE PERMISO!
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    // 5. Continuar con el resto de filtros
    filterChain.doFilter(request, response);
  }
}