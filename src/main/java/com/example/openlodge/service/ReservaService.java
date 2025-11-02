package com.example.openlodge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.openlodge.dto.ReservaRequest;
import com.example.openlodge.model.Propiedad;
import com.example.openlodge.model.Reserva;
import com.example.openlodge.model.Usuario;
import com.example.openlodge.repository.PropiedadRepository;
import com.example.openlodge.repository.ReservaRepository;
import com.example.openlodge.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PropiedadRepository propiedadRepository;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository,
                          UsuarioRepository usuarioRepository,
                          PropiedadRepository propiedadRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.propiedadRepository = propiedadRepository;
    }

    /**
     * Crea una reserva nueva.
     */
    @Transactional
    public Reserva crearReserva(ReservaRequest request, String emailHuesped) {
        
        // 1. Buscar al Huésped por su email (del token)
        Usuario huesped = usuarioRepository.findByEmail(emailHuesped)
                .orElseThrow(() -> new EntityNotFoundException("Huésped no encontrado con email: " + emailHuesped));

        // 2. Buscar la Propiedad por su ID (del DTO)
        Propiedad propiedad = propiedadRepository.findById(request.getPropiedadId())
                .orElseThrow(() -> new EntityNotFoundException("Propiedad no encontrada con ID: " + request.getPropiedadId()));

        // 3. Crear la nueva entidad Reserva
        Reserva nuevaReserva = new Reserva();
        
        // 4. Llenar los datos desde el DTO
        nuevaReserva.setFechaInicio(request.getFechaInicio());
        nuevaReserva.setFechaFin(request.getFechaFin());
        nuevaReserva.setPrecioTotal(request.getPrecioTotal());
        nuevaReserva.setNotas(request.getNotas());
        
        // 5. Asignar las relaciones
        nuevaReserva.setHuesped(huesped);
        nuevaReserva.setPropiedad(propiedad);

        // 6. Guardar y devolver la reserva creada
        return reservaRepository.save(nuevaReserva);
    }
    
    // (Aquí irán los métodos de GET /mis-reservas, etc.)
}
