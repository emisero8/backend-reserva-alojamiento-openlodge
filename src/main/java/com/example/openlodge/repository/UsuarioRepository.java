package com.example.openlodge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.openlodge.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Esto ya trae métodos como: Con el extend.
    //  save(usuario): Guarda un usuario nuevo o actualiza uno existente.
    //  findById(id): Busca un usuario por su ID.
    //  findAll(): Devuelve todos los usuarios.
    //  deleteById(id): Borra un usuario.

    Optional<Usuario> findByEmail(String email);
}
