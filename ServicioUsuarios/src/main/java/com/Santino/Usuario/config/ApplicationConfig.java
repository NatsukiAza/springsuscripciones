package com.Santino.Usuario.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.Santino.Usuario.repository.UsuarioRepository;

@Configuration
public class ApplicationConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
    return username -> usuarioRepository.findByUsername(username)
        .map(usuario -> User.withUsername(usuario.getUsername())
            .password(usuario.getPassword())
            .authorities(List.of())
            .build())
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
  }

}