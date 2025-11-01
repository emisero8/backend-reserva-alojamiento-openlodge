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
    // 1. Inyectamos la dependencia del Repositorio.
    // El Servicio necesita el Repositorio para hablar con la BD.
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // ⬅️ AÑADIR EL ENCODER

    // 2. Constructor para la Inyección de Dependencias
    // (Esta es la forma moderna de @Autowired)
    @Autowired
    public UsuarioService (UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder; // ⬅️ 3. ASIGNARLO
    }

    // --- 3. Métodos con la Lógica de Negocio ---
    /**
     * Obtiene todos los usuarios de la base de datos.
     */
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    /**
     * Obtiene un usuario específico por su ID.
     * Usamos Optional por si el usuario no existe.
     */
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Crea y guarda un nuevo usuario.
     * (Más adelante, aquí agregaremos la lógica para
     * encriptar la contraseña antes de guardarla)
     */
    /*public Usuario crearUsuario(Usuario usuario) {
        // Lógica futura:
        // if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
        //    throw new Exception("El email ya existe");
        // }
        // usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        return usuarioRepository.save(usuario);
    } */

    /**
     * Crea y guarda un nuevo usuario.
     * ¡AHORA ENCRIPTA LA CONTRASEÑA!
     */
    public Usuario crearUsuario(Usuario usuario) {
        
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya se encuentra registrado.");
        }

        // 4. ¡ACCIÓN! Usamos el encoder antes de guardar
        String contrasenaHasheada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(contrasenaHasheada);

        return usuarioRepository.save(usuario);
    }

    // (Aquí podríamos agregar métodos como actualizarUsuario, borrarUsuario, etc.)

}
