package com.example.openlodge.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.openlodge.model.Propiedad;
import com.example.openlodge.service.PropiedadService;

@RestController
@RequestMapping("/api/propiedades")
public class PropiedadController {
    // 1. Inyectamos el Servicio (el Controlador habla con el Servicio)
    private final PropiedadService propiedadService;

    @Autowired
    public PropiedadController(PropiedadService propiedadService) {
        this.propiedadService = propiedadService;
    }

    // --- Endpoints de la API ---

    /**
     * Endpoint para OBTENER TODAS las propiedades.
     * Se activa con: GET http://localhost:8080/api/propiedades
     */
    @GetMapping
    public List<Propiedad> obtenerTodasLasPropiedades() {
        return propiedadService.obtenerTodasLasPropiedades();
    }

    /**
     * Endpoint para OBTENER UNA propiedad por su ID.
     * Se activa con: GET http://localhost:8080/api/propiedades/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<Propiedad> obtenerPropiedadPorId(@PathVariable Long id) {
        return propiedadService.obtenerPropiedadPorId(id)
                .map(ResponseEntity::ok) // Si la encuentra, devuelve 200 OK + propiedad
                .orElse(ResponseEntity.notFound().build()); // Si no, devuelve 404 Not Found
    }

    /**
     * Endpoint para OBTENER todas las propiedades de UN ANFITRIÓN.
     * Se activa con: GET http://localhost:8080/api/propiedades/anfitrion/1
     */
    @GetMapping("/anfitrion/{anfitrionId}")
    public List<Propiedad> obtenerPropiedadesPorAnfitrion(@PathVariable Long anfitrionId) {
        // Esto usa el método que creamos en el Repository/Service
        return propiedadService.obtenerPropiedadesPorAnfitrion(anfitrionId);
    }

    /**
     * Endpoint para CREAR una nueva propiedad para un anfitrión.
     * Se activa con: POST http://localhost:8080/api/propiedades/anfitrion/1
     * @param anfitrionId El ID del anfitrión (dueño) viene de la URL
     * @param propiedad   El JSON de la propiedad viene en el Body
     */
    @PostMapping("/anfitrion/{anfitrionId}")
    public ResponseEntity<Propiedad> crearPropiedad(
            @PathVariable Long anfitrionId,
            @RequestBody Propiedad propiedad) {
        
        // El servicio se encarga de buscar al anfitrión y asignarlo
        Propiedad nuevaPropiedad = propiedadService.crearPropiedad(propiedad, anfitrionId);
        return new ResponseEntity<>(nuevaPropiedad, HttpStatus.CREATED);
    }

    /**
     * Endpoint para ASIGNAR un servicio a una propiedad.
     * El anfitrión (en la app) seleccionará su propiedad y luego
     * hará clic en un servicio (ej: "WIFI" con id 1).
     *
     * Se activa con: POST http://localhost:8080/api/propiedades/1/servicios/1
     * (Asigna servicio 1 a propiedad 1)
     */
    @PostMapping("/{propiedadId}/servicios/{servicioId}")
    public ResponseEntity<Propiedad> agregarServicioAPropiedad(
            @PathVariable Long propiedadId,
            @PathVariable Long servicioId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // 1. Obtenemos el email del usuario logueado
        String emailUsuarioLogueado = userDetails.getUsername();

        // 2. Pasamos el email al servicio para la validación
        Propiedad propiedadActualizada = propiedadService.agregarServicioAPropiedad(
                propiedadId, 
                servicioId, 
                emailUsuarioLogueado // ⬅️ Pasamos el email
        );
        
        return ResponseEntity.ok(propiedadActualizada);
    }

    /**
     * ¡EXTRA! Un manejador de excepciones.
     * Si el PropiedadService lanza el error "Anfitrión no encontrado...",
     * este método lo captura y devuelve un 404 en lugar de un 500 feo.
     */

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        // Esto maneja el 404 (Not Found)
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        // Esto maneja el 403 (Forbidden)
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }
}