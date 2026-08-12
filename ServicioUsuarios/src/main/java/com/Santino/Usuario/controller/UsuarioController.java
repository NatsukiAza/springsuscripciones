package com.Santino.Usuario.controller;

import org.springframework.web.bind.annotation.RestController;

import com.Santino.Usuario.dto.UsuarioRequest;
import com.Santino.Usuario.service.UsuarioService;
import com.Santino.Usuario.entity.Usuario;
import com.Santino.Usuario.config.SecurityConfig;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioService usuarioService, PasswordEncoder passwordEncoder){
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/registrarse")
    public ResponseEntity<UsuarioRequest> crearUsuario(@RequestBody UsuarioRequest request) {
        
        UsuarioRequest usuarioGuardado = usuarioService.crearUsuario(request);
        return ResponseEntity.ok(usuarioGuardado);
    }
    
    @PostMapping("/login")
    public ResponseEntity<UsuarioRequest> inicioSesion(@RequestBody UsuarioRequest request) {

        Usuario usuarioGuardado = usuarioService.
        return entity;
    }
    

}
