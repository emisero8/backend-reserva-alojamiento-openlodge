package com.example.openlodge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.openlodge.model.Propiedad;

@Repository
public interface PropiedadRepository extends JpaRepository<Propiedad, Long> {
    // --- Método "Mágico" de Spring Data JPA ---
    
    // Al escribir un método que se llama "findBy" seguido del
    // nombre de un campo de la entidad (en este caso "anfitrionId"),
    // Spring entiende qué consulta SQL debe crear.
    
    // Esto crea automáticamente: "SELECT * FROM propiedades WHERE anfitrion_id = ?"
    List<Propiedad> findByAnfitrionId(Long anfitrionId);
}
