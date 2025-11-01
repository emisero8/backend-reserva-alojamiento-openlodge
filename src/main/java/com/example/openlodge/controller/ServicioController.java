package com.example.openlodge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.openlodge.model.Servicio;
import com.example.openlodge.service.ServicioService;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {
    private final ServicioService servicioService;

    @Autowired
    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    /**
     * Endpoint para OBTENER TODOS los servicios maestros.
     * Tu app de React Native llamará a esto para mostrarle al anfitrión
     * la lista de checkboxes (WIFI, Pileta, etc.) que puede agregar.
     *
     * Se activa con: GET http://localhost:8080/api/servicios
     */
    @GetMapping
    public List<Servicio> obtenerTodosLosServicios() {
        return servicioService.obtenerTodosLosServicios();
    }

    /**
     * Endpoint para CREAR un nuevo servicio maestro.
     * (Este endpoint es 'privado', solo para admins, pero por ahora
     * lo usaremos para cargar los datos de tu JSON).
     *
     * Se activa con: POST http://localhost:8080/api/servicios
     */
   // @PostMapping
    //public ResponseEntity<Servicio> crearServicio(@RequestBody Servicio servicio) {
     //   Servicio nuevoServicio = servicioService.crearServicio(servicio);
      //  return new ResponseEntity<>(nuevoServicio, HttpStatus.CREATED);
    //}
}
