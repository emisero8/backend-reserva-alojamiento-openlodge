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
    private String titulo; // Ej: "Casa de campo con pileta"

    @Column(nullable = false, length = 1000)
    private String descripcion; // Descripción larga

    @Column(nullable = false)
    private String direccion; // Aquí va el JSON 'title': "General Lopez 3234..."

    @Column(nullable = false)
    private Double precioPorNoche; // Aquí va el JSON 'price'

    @Column(nullable = false)
    private int numeroHuespedes; // Max. cantidad de personas

    // --- ESTOS DOS SE DETALLAN EN LA DESCRIPCION Y LISTO ---
    //@Column(nullable = false)
    //private int habitaciones; // De "2 habitaciones"

    //@Column(nullable = false)
    //private int banos; // De "1 baño"

    // --- Campo para la imagen ---
    @Column(name = "imagen_principal_url")
    private String imagenPrincipalUrl; // Aquí va el JSON 'img'

    // --- Relación con el Anfitrión ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anfitrion_id", nullable = false)
    private Usuario anfitrion;

    // --- ¡NUEVO! Para la lista de "servicios" ---
    // Esto crea una tabla separada llamada "propiedad_servicios"
    // que contendrá "WIFI", "Pileta", etc., asociadas a esta propiedad.
    //@ElementCollection(fetch = FetchType.EAGER) // EAGER: Cargar los servicios junto con la propiedad
    //@CollectionTable(name = "propiedad_servicios", joinColumns = @JoinColumn(name = "propiedad_id"))
    //@Column(name = "servicio", nullable = false)

    @ManyToMany(fetch = FetchType.EAGER) // EAGER: Cargar los servicios con la propiedad
    @JoinTable(
        name = "propiedad_x_servicio", // tabla intermedia
        joinColumns = @JoinColumn(name = "propiedad_id"),
        inverseJoinColumns = @JoinColumn(name = "servicio_id")
    )

    private Set<Servicio> servicios = new HashSet<>(); // Usamos Set para evitar duplicados

}