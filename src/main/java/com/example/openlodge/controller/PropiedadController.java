package com.example.openlodge.controller;

// 1. ¡IMPORT CORREGIDO! Esta es la excepción de Spring Security
import org.springframework.security.access.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 2. ¡NUEVO IMPORT! Esta es la excepción de "No Encontrado"
import jakarta.persistence.EntityNotFoundException;

import com.example.openlodge.model.Propiedad;
import com.example.openlodge.service.PropiedadService;

@RestController
@RequestMapping("/api/propiedades")
@CrossOrigin(origins = "http://localhost:8081")
public class PropiedadController {

    private final PropiedadService propiedadService;

    @Autowired
    public PropiedadController(PropiedadService propiedadService) {
        this.propiedadService = propiedadService;
    }

    // --- (Todos tus endpoints GET y POST quedan exactamente igual) ---

    @GetMapping
    public List<Propiedad> obtenerTodasLasPropiedades() {
        return propiedadService.obtenerTodasLasPropiedades();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Propiedad> obtenerPropiedadPorId(@PathVariable Long id) {
        return propiedadService.obtenerPropiedadPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/anfitrion/{anfitrionId}")
    public List<Propiedad> obtenerPropiedadesPorAnfitrion(@PathVariable Long anfitrionId) {
        return propiedadService.obtenerPropiedadesPorAnfitrion(anfitrionId);
    }

    @PostMapping
    public ResponseEntity<Propiedad> crearPropiedad(
            @RequestBody Propiedad propiedad,
            @AuthenticationPrincipal UserDetails userDetails) {

        // 1. Obtenemos el email del token
        String emailAnfitrion = userDetails.getUsername();

        // 2. Llamamos al servicio actualizado
        Propiedad nuevaPropiedad = propiedadService.crearPropiedad(propiedad, emailAnfitrion);

        return new ResponseEntity<>(nuevaPropiedad, HttpStatus.CREATED);
    }

    @PostMapping("/{propiedadId}/servicios/{servicioId}")
    public ResponseEntity<Propiedad> agregarServicioAPropiedad(
            @PathVariable Long propiedadId,
            @PathVariable Long servicioId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String emailUsuarioLogueado = userDetails.getUsername();
        Propiedad propiedadActualizada = propiedadService.agregarServicioAPropiedad(
                propiedadId,
                servicioId,
                emailUsuarioLogueado);
        return ResponseEntity.ok(propiedadActualizada);
    }

    @GetMapping("/mis-propiedades")
    public List<Propiedad> obtenerMisPropiedades(
            @AuthenticationPrincipal UserDetails userDetails) {
        String emailUsuarioLogueado = userDetails.getUsername();
        return propiedadService.obtenerPropiedadesPorEmailAnfitrion(emailUsuarioLogueado);
    }

    // --- 3. MANEJADORES DE EXCEPCIONES CORREGIDOS ---

    /**
     * Atrapa SÓLO los errores de "No Encontrado" (404)
     * que lanzamos desde el servicio (usando EntityNotFoundException).
     */
    @ExceptionHandler(EntityNotFoundException.class) // ⬅️ ¡CAMBIO IMPORTANTE!
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Atrapa los errores de "Acceso Denegado" (403)
     * (Ahora usa la importación correcta de Spring Security)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    // ¡Ya no existe el manejador genérico de RuntimeException!
}