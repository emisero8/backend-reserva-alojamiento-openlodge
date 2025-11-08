package com.example.openlodge.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.openlodge.model.Servicio;
import com.example.openlodge.repository.ServicioRepository;

@Service
public class ServicioService {
    private final ServicioRepository servicioRepository;

    @Autowired
    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    /**
     * Obtiene la lista completa de servicios disponibles en el sistema
     */
    public List<Servicio> obtenerTodosLosServicios() {
        return servicioRepository.findAll();
    }

    /**
     * Crea un nuevo servicio maestro
     */
    public Servicio crearServicio(Servicio servicio) {
        // Podríamos agregar validación para que no se repita el nombre
        return servicioRepository.save(servicio);
    }
}
