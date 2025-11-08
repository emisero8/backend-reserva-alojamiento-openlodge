package com.example.openlodge.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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
                .orElseThrow(() -> new EntityNotFoundException(
                        "Propiedad no encontrada con ID: " + request.getPropiedadId()));

        // 3. Validacion de disponibilidad
        List<Reserva> overlappingReservas = reservaRepository.findOverlappingReservas(
                request.getPropiedadId(),
                request.getFechaInicio(),
                request.getFechaFin()
        );

        if (!overlappingReservas.isEmpty()) {
            // Lanzamos un error que el Controller atrapará
            throw new IllegalStateException("Las fechas seleccionadas ya no están disponibles.");
        }

        // 4. Crear la nueva entidad Reserva
        Reserva nuevaReserva = new Reserva();

        // 5. Llenar los datos desde el DTO
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

    /**
     * Obtiene todas las reservas hechas POR un huésped
     */
    public List<Reserva> obtenerMisReservas(String emailHuesped) {
        // 1. Buscamos al Huésped por su email (del token)
        Usuario huesped = usuarioRepository.findByEmail(emailHuesped)
                .orElseThrow(() -> new EntityNotFoundException("Huésped no encontrado con email: " + emailHuesped));

        // 2. Llamamos al repositorio
        return reservaRepository.findByHuespedId(huesped.getId());
    }

    /**
     * Obtiene todas las reservas hechas A las propiedades de un Anfitrión
     */
    public List<Reserva> obtenerReservasDeMisPropiedades(String emailAnfitrion) {
        // 1. Buscamos al Anfitrión por su email (del token)
        Usuario anfitrion = usuarioRepository.findByEmail(emailAnfitrion)
                .orElseThrow(() -> new EntityNotFoundException("Anfitrión no encontrado con email: " + emailAnfitrion));

        // 2. Llamamos al repositorio
        return reservaRepository.findByPropiedadAnfitrionId(anfitrion.getId());
    }

    /**
     * Cancela (borra) una reserva
     * Valida que el usuario logueado sea el Anfitrión de la propiedad reservada
     */
    @Transactional
    public void cancelarReserva(Long reservaId, String emailAnfitrion) {

        // 1. Buscamos la reserva
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con ID: " + reservaId));

        // 2. Buscamos al anfitrión (el usuario logueado)
        Usuario anfitrion = usuarioRepository.findByEmail(emailAnfitrion)
                .orElseThrow(() -> new EntityNotFoundException("Anfitrión no encontrado con email: " + emailAnfitrion));

        // 3. Validamos que este anfitrión sea el dueño de la propiedad reservada
        Propiedad propiedadReservada = reserva.getPropiedad();
        if (!propiedadReservada.getAnfitrion().getId().equals(anfitrion.getId())) {
            throw new AccessDeniedException("No tienes permiso para cancelar esta reserva.");
        }

        // 4. Borramos la reserva
        reservaRepository.delete(reserva);
    }

    /**
     * (Para el Huésped)
     * Cancela (borra) una reserva, validando que el usuario sea el dueño
     * Y que la reserva aún no haya comenzado.
     */
    @Transactional
    public void cancelarMiReserva(Long reservaId, String emailHuesped) {
        
        // 1. Buscamos al Huésped (el usuario logueado)
        Usuario huesped = usuarioRepository.findByEmail(emailHuesped)
                .orElseThrow(() -> new EntityNotFoundException("Huésped no encontrado con email: " + emailHuesped));

        // 2. Buscamos la reserva
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con ID: " + reservaId));

        // 3. Validación de Propietario
        if (!reserva.getHuesped().getId().equals(huesped.getId())) {
            throw new AccessDeniedException("No tienes permiso para cancelar esta reserva.");
        }

        // 4. Validación de Fecha
        LocalDate hoy = LocalDate.now();
        if (reserva.getFechaInicio().isBefore(hoy) || reserva.getFechaInicio().isEqual(hoy)) {
            // Si la reserva es para hoy o ya pasó, no se puede cancelar
            throw new IllegalStateException("No se puede cancelar una reserva que ya ha comenzado o es para hoy.");
        }

        // 5. Borramos la reserva
        reservaRepository.delete(reserva);
    }
}
