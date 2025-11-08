package com.example.openlodge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.openlodge.model.Propiedad;

@Repository
public interface PropiedadRepository extends JpaRepository<Propiedad, Long> {
    
    // "SELECT * FROM propiedades WHERE anfitrion_id = ?"
    List<Propiedad> findByAnfitrionId(Long anfitrionId);
}
