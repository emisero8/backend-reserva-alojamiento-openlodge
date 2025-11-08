package com.example.openlodge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.openlodge.model.Servicio;
import com.example.openlodge.service.ServicioService;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "*")
public class ServicioController {
    private final ServicioService servicioService;

    @Autowired
    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    /**
     * Endpoint para OBTENER TODOS los servicios maestros.
     *
     * Se activa con: GET http://localhost:8080/api/servicios
     */
    @GetMapping
    public List<Servicio> obtenerTodosLosServicios() {
        return servicioService.obtenerTodosLosServicios();
    }

}
