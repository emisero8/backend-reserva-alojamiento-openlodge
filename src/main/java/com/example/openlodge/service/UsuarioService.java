package com.example.openlodge.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.openlodge.model.Usuario;
import com.example.openlodge.repository.UsuarioRepository;

@Service
public class UsuarioService {
    // 1. Inyectamos la dependencia del Repositorio
    // El Servicio necesita el Repositorio para hablar con la BD
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // 2. Constructor para la Inyección de Dependencias
    @Autowired
    public UsuarioService (UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- Métodos con la Lógica de Negocio ---
    /**
     * Obtiene todos los usuarios de la base de datos
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    /**
     * Obtiene un usuario específico por su ID
     * Usamos Optional por si el usuario no existe
     */
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Crea y guarda un nuevo usuario.
     */
    public Usuario crearUsuario(Usuario usuario) {
        
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya se encuentra registrado.");
        }

        // Usamos el encoder antes de guardar
        String contrasenaHasheada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(contrasenaHasheada);

        return usuarioRepository.save(usuario);
    }

    // (Aca podríamos agregar métodos como actualizarUsuario, borrarUsuario, etc.)

}
