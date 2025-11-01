package com.example.openlodge.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.openlodge.model.Usuario;
import com.example.openlodge.service.UsuarioService;

@RestController // Le dice a Spring que esta clase es un Controlador API REST
@RequestMapping("/api/usuarios") // La URL base para todos los métodos de esta clase
@CrossOrigin(origins = "http://localhost:8081")
public class UsuarioController {
    // 1. Inyectamos el Servicio (el Controlador habla con el Servicio)
    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // --- Endpoints de la API ---

    /**
     * Endpoint para CREAR un nuevo usuario.
     * Se activa con: POST http://localhost:8080/api/usuarios
     * @param usuario El objeto Usuario vendrá en el body JSON de la petición
     */
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);
        // Devolvemos un 201 Created (el estándar para POST exitoso)
        return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
    }

    /**
     * Endpoint para OBTENER TODOS los usuarios.
     * Se activa con: GET http://localhost:8080/api/usuarios
     */
    @GetMapping
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioService.obtenerTodosLosUsuarios();
    }

    /**
     * Endpoint para OBTENER UN usuario por su ID.
     * Se activa con: GET http://localhost:8080/api/usuarios/1 (por ejemplo)
     * @param id El ID vendrá de la URL
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorId(id);
        
        // Usamos ResponseEntity para poder devolver un 404 si no se encuentra
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get()); // Devuelve 200 OK + el usuario
        } else {
            return ResponseEntity.notFound().build(); // Devuelve 404 Not Found
        }
    }

    /**
     * Atrapa la excepción que lanzamos desde el servicio
     * si el email ya existe.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        // Devolvemos un 409 Conflict (un error semántico
        // que dice "la petición no se completó por un conflicto")
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

}