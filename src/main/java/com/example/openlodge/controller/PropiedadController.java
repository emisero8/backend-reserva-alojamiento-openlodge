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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    /**
     * Endpoint para BORRAR (dar de baja) una propiedad.
     * Usa el token para validar que el usuario es el dueño.
     *
     * Se activa con: DELETE http://localhost:8080/api/propiedades/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarPropiedad(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // 1. Obtenemos el email del anfitrión (dueño) desde el token
        String emailAnfitrion = userDetails.getUsername();

        // 2. Llamamos al servicio para que valide y borre
        propiedadService.borrarPropiedad(id, emailAnfitrion);

        // 3. Devolvemos un 204 No Content (estándar para DELETE exitoso)
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para ACTUALIZAR (Editar) una propiedad existente.
     * Usa el token para validar que el usuario es el dueño.
     *
     * Se activa con: PUT http://localhost:8080/api/propiedades/1
     */
    @PutMapping("/{id}")
    public ResponseEntity<Propiedad> actualizarPropiedad(
            @PathVariable Long id,
            @RequestBody Propiedad datosPropiedad,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // 1. Obtenemos el email del anfitrión (dueño) desde el token
        String emailAnfitrion = userDetails.getUsername();

        // 2. Llamamos al servicio para que valide y actualice
        Propiedad propiedadActualizada = propiedadService.actualizarPropiedad(id, datosPropiedad, emailAnfitrion);

        // 3. Devolvemos un 200 OK con la propiedad actualizada
        return ResponseEntity.ok(propiedadActualizada);
    }

    // ---  MANEJADORES DE EXCEPCIONES  ---

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