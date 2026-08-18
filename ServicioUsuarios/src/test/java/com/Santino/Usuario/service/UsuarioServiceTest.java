package com.Santino.Usuario.service;

import com.Santino.Usuario.dto.LoginRequest;
import com.Santino.Usuario.dto.UsuarioRequest;
import com.Santino.Usuario.entity.Usuario;
import com.Santino.Usuario.exception.CredentialsException;
import com.Santino.Usuario.exception.UserAlreadyExists;
import com.Santino.Usuario.repository.UsuarioRepository;
import com.Santino.Usuario.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("crearUsuario: hashea la contraseña y persiste al usuario nuevo")
    void crearUsuarioNuevo() {
        UsuarioRequest request = new UsuarioRequest("santi", "santi@streamsub.com", "clave-plana");
        when(passwordEncoder.encode("clave-plana")).thenReturn("hash-bcrypt");
        when(usuarioRepository.findByUsername("santi")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            ReflectionTestUtils.setField(usuario, "ID", 1L);
            return usuario;
        });

        UsuarioRequest respuesta = usuarioService.crearUsuario(request);

        ArgumentCaptor<Usuario> persistido = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(persistido.capture());
        assertThat(persistido.getValue().getUsername()).isEqualTo("santi");
        assertThat(persistido.getValue().getEmail()).isEqualTo("santi@streamsub.com");
        assertThat(persistido.getValue().getPassword()).isEqualTo("hash-bcrypt");
        assertThat(respuesta).isEqualTo(
                new UsuarioRequest("santi", "santi@streamsub.com", "hash-bcrypt"));
    }

    @Test
    @DisplayName("crearUsuario: no persiste si el username ya existe")
    void crearUsuarioDuplicado() {
        UsuarioRequest request = new UsuarioRequest("santi", "santi@streamsub.com", "clave-plana");
        when(passwordEncoder.encode("clave-plana")).thenReturn("hash-bcrypt");
        when(usuarioRepository.findByUsername("santi"))
                .thenReturn(Optional.of(new Usuario("santi", "otro@mail.com", "otro-hash")));

        assertThatThrownBy(() -> usuarioService.crearUsuario(request))
                .isInstanceOf(UserAlreadyExists.class)
                .hasMessage("El nombre de usuario ya existe");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("iniciarSesion: emite JWT cuando usuario y contraseña coinciden")
    void iniciarSesionExitoso() {
        Usuario usuario = new Usuario("santi", "santi@streamsub.com", "hash-bcrypt");
        when(usuarioRepository.findByUsername("santi")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("clave-plana", "hash-bcrypt")).thenReturn(true);
        when(jwtService.generarToken(usuario)).thenReturn("jwt-firmado");

        String token = usuarioService.iniciarSesion(new LoginRequest("santi", "clave-plana"));

        assertThat(token).isEqualTo("jwt-firmado");
    }

    @Test
    @DisplayName("iniciarSesion: rechaza un username inexistente")
    void iniciarSesionUsuarioInexistente() {
        when(usuarioRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.iniciarSesion(new LoginRequest("ghost", "clave")))
                .isInstanceOf(CredentialsException.class)
                .hasMessage("La contraseña o el usuario ingresado es incorrectaaaa");

        verify(jwtService, never()).generarToken(any());
    }

    @Test
    @DisplayName("iniciarSesion: rechaza una contraseña que no matchea el hash")
    void iniciarSesionPasswordIncorrecta() {
        Usuario usuario = new Usuario("santi", "santi@streamsub.com", "hash-bcrypt");
        when(usuarioRepository.findByUsername("santi")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("otra-clave", "hash-bcrypt")).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.iniciarSesion(new LoginRequest("santi", "otra-clave")))
                .isInstanceOf(CredentialsException.class)
                .hasMessage("La contraseña o el usuario ingresado es incorrecta");

        verify(jwtService, never()).generarToken(any());
    }
}
