package com.example.openlodge.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.openlodge.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
       /**
        * Encuentra todas las reservas de un huésped específico,
        * e incluye (FETCH) los datos de la Propiedad asociada.
        */
       @Query("SELECT r FROM Reserva r JOIN FETCH r.propiedad WHERE r.huesped.id = :huespedId")
       List<Reserva> findByHuespedId(Long huespedId);

       /**
        * * Encuentra todas las reservas de las propiedades de un anfitrión,
        * e incluye (FETCH) los datos de la Propiedad Y del Huésped.
        */
       @Query("SELECT r FROM Reserva r JOIN FETCH r.propiedad JOIN FETCH r.huesped WHERE r.propiedad.anfitrion.id = :anfitrionId")
       List<Reserva> findByPropiedadAnfitrionId(Long anfitrionId);

       /**
        * Busca reservas que se superpongan con un rango de fechas dado.
        *
        * Una reserva (A) se superpone con otra (B) si:
        * (A.Inicio < B.Fin) Y (A.Fin > B.Inicio)
        */
       @Query("SELECT r FROM Reserva r WHERE r.propiedad.id = :propiedadId " +
                     "AND r.fechaInicio < :fechaFin " +
                     "AND r.fechaFin > :fechaInicio")
       List<Reserva> findOverlappingReservas(
                     Long propiedadId,
                     LocalDate fechaInicio,
                     LocalDate fechaFin);

       /**
        * Busca todas las reservas asociadas a un ID de propiedad.
        */
       List<Reserva> findByPropiedadId(Long propiedadId);
}
