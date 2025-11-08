package com.example.openlodge.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(exclude = {"anfitrion","servicios"})
@Entity
@Table(name = "propiedades")
public class Propiedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descripcion; // Descripción larga

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private Double precioPorNoche;

    @Column(nullable = false)
    private int numeroHuespedes; // Max. cantidad de personas

    // Campo para la imagen
    @Column(name = "imagen_principal_url")
    private String imagenPrincipalUrl; // Aquí va el JSON 'img'

    // Relación con el Anfitrión
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anfitrion_id", nullable = false)
    private Usuario anfitrion;


    // Esto crea una tabla separada "propiedad_servicios"
    // que contiene "WIFI", "Pileta", etc asociadas a la propiedad
    @ManyToMany(fetch = FetchType.EAGER) // EAGER: Cargar los servicios con la propiedad
    @JoinTable(
        name = "propiedad_x_servicio", // tabla intermedia
        joinColumns = @JoinColumn(name = "propiedad_id"),
        inverseJoinColumns = @JoinColumn(name = "servicio_id")
    )

    private Set<Servicio> servicios = new HashSet<>();
}