package com.example.openlodge.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="servicios")
public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private Double costo;

    // Esta es la "otra cara" de la relación.
    // Le dice a JPA: "No crees una columna 'propiedades' aquí,
    // la entidad Propiedad ya se encarga de la relación".
    @ManyToMany(mappedBy = "servicios")
    @JsonIgnore // ¡Importante! Evita bucles infinitos al convertir a JSON
    private Set<Propiedad> propiedades;
}
