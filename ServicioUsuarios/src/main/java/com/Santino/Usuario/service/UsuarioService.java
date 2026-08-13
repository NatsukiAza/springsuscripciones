package com.Santino.Usuario.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.Santino.Usuario.repository.UsuarioRepository;
import com.Santino.Usuario.entity.Usuario;
import com.Santino.Usuario.dto.UsuarioRequest;
import com.Santino.Usuario.dto.LoginRequest;
import com.Santino.Usuario.security.JwtService;

import java.util.Optional;

import com.Santino.Usuario.exception.CredentialsException;
import com.Santino.Usuario.exception.UserAlreadyExists;

@Service
public class UsuarioService {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UsuarioRequest crearUsuario(UsuarioRequest usuarioRequest) {

        Usuario usuarioEntidad = new Usuario(usuarioRequest.username(), usuarioRequest.email(),
                passwordEncoder.encode(usuarioRequest.password()));

        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByUsername(usuarioEntidad.getUsername());
        if (usuarioEncontrado.isPresent())
            throw new UserAlreadyExists("El nombre de usuario ya existe");
        Usuario usuarioGuardado = usuarioRepository.save(usuarioEntidad);
        return new UsuarioRequest(usuarioGuardado.getUsername(), usuarioGuardado.getEmail(),
                usuarioGuardado.getPassword());

    }

    public String iniciarSesion(LoginRequest usuarioRequest) {

        Optional<Usuario> usuario = usuarioRepository.findByUsername(usuarioRequest.username());

        if (usuario.isEmpty())
            throw new CredentialsException("La contraseña o el usuario ingresado es incorrectaaaa");

        if (!passwordEncoder.matches(usuarioRequest.password(), usuario.get().getPassword()))
            throw new CredentialsException("La contraseña o el usuario ingresado es incorrecta");

        return jwtService.generarToken(usuario.get());
    }
}
