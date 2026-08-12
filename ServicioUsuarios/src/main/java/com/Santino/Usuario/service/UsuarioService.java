package com.Santino.Usuario.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.Santino.Usuario.repository.UsuarioRepository;
import com.Santino.Usuario.entity.Usuario;
import com.Santino.Usuario.dto.UsuarioRequest;
@Service
public class UsuarioService {
    
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioRequest crearUsuario(UsuarioRequest usuarioRequest){

        Usuario usuarioEntidad = new Usuario(usuarioRequest.username(), usuarioRequest.email(), passwordEncoder.encode(usuarioRequest.password()));
        Usuario usuarioGuardado = usuarioRepository.save(usuarioEntidad);
        return new UsuarioRequest(usuarioGuardado.getUsername(), usuarioGuardado.getEmail(), usuarioGuardado.getPassword());

    }

    public UsuarioRequest iniciarSesion(UsuarioRequest usuarioRequest){
        
    }
}
