package com.Santino.Usuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Santino.Usuario.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

}
