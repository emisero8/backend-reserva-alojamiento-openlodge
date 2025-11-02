package com.example.openlodge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.openlodge.dto.ReservaRequest;
import com.example.openlodge.model.Reserva;
import com.example.openlodge.service.ReservaService;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "http://localhost:8081")
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
}
