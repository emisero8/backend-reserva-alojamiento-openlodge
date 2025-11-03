package com.example.openlodge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.openlodge.dto.ReservaRequest;
import com.example.openlodge.model.Reserva;
import com.example.openlodge.service.ReservaService;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {
    private final ReservaService reservaService;

    @Autowired
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    /**
     * Endpoint para CREAR una nueva reserva.
     * Usará el token del Huésped.
     *
     * Se activa con: POST http://localhost:8080/api/reservas
     */
    @PostMapping
    public ResponseEntity<Reserva> crearReserva(
            @RequestBody ReservaRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // 1. Obtenemos el email del Huésped desde el token
        String emailHuesped = userDetails.getUsername();

        // 2. Llamamos al servicio para crear la reserva
        Reserva nuevaReserva = reservaService.crearReserva(request, emailHuesped);
        
        // 3. Devolvemos 201 Created
        return new ResponseEntity<>(nuevaReserva, HttpStatus.CREATED);
    }

    /**
     * Obtiene el historial de reservas del huésped logueado.
     *
     * Se activa con: GET http://localhost:8080/api/reservas/mis-reservas
     */
    @GetMapping("/mis-reservas")
    public List<Reserva> obtenerMisReservas(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String emailHuesped = userDetails.getUsername();
        return reservaService.obtenerMisReservas(emailHuesped);
    }

    /**
     * Obtiene todas las reservas hechas a las propiedades del anfitrión logueado.
     *
     * Se activa con: GET http://localhost:8080/api/reservas/de-mis-propiedades
     */
    @GetMapping("/de-mis-propiedades")
    public List<Reserva> obtenerReservasDeMisPropiedades(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String emailAnfitrion = userDetails.getUsername();
        return reservaService.obtenerReservasDeMisPropiedades(emailAnfitrion);
    }

    /**
     * Endpoint para BORRAR (cancelar) una reserva.
     * (Usado por el Anfitrión en 'MenuGestionar')
     *
     * Se activa con: DELETE http://localhost:8080/api/reservas/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarReserva(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // 1. Obtenemos el email del Anfitrión desde el token
        String emailAnfitrion = userDetails.getUsername();

        // 2. Llamamos al servicio para que valide y borre
        reservaService.cancelarReserva(id, emailAnfitrion);

        // 3. Devolvemos un 204 No Content (éxito)
        return ResponseEntity.noContent().build();
    }
}
