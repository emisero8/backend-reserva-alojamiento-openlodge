package com.example.openlodge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.openlodge.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    /**
     * Encuentra todas las reservas de un huésped específico.
     * (Para la pantalla "Mis Reservas" del Huésped)
     */
    List<Reserva> findByHuespedId(Long huespedId);

    /**
     * Encuentra todas las reservas de todas las propiedades
     * que pertenecen a un anfitrión específico.
     * (Para las pantallas "MenuGestionar" y "MenuHistorial" del Anfitrión)
     */
    List<Reserva> findByPropiedadAnfitrionId(Long anfitrionId);
}
