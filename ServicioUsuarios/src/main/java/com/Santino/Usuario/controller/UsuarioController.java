package com.Santino.Usuario.controller;

import org.springframework.web.bind.annotation.RestController;

import com.Santino.Usuario.dto.UsuarioRequest;
import com.Santino.Usuario.service.UsuarioService;
import com.Santino.Usuario.dto.LoginRequest;
import com.Santino.Usuario.dto.AuthResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/auth/registrarse")
    public ResponseEntity<UsuarioRequest> crearUsuario(@RequestBody UsuarioRequest request) {

        UsuarioRequest usuarioGuardado = usuarioService.crearUsuario(request);
        return ResponseEntity.ok(usuarioGuardado);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> inicioSesion(@RequestBody LoginRequest request) {

        String token = usuarioService.iniciarSesion(request);

        return ResponseEntity.ok(new AuthResponse(token));
    }

}
