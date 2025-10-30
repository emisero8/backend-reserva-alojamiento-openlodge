package com.example.openlodge.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // ⬅️ Lombok: Crea getters, setters, toString(), equals(), hashCode()
@NoArgsConstructor // ⬅️ Lombok: Crea un constructor vacío (necesario para JPA)
@AllArgsConstructor // ⬅️ Lombok: Crea un constructor con todos los argumentos
@Entity // ⬅️ JPA: Le dice a Spring que esta clase es una tabla de BD
@Table(name = "usuarios") // ⬅️ JPA: Nombra la tabla (en plural es buena práctica)
public class Usuario {

    @Id // ⬅️ JPA: Marca esto como la Clave Primaria (Primary Key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ⬅️ JPA: Hace que el ID sea autoincremental
    private Long id;

    @Column(nullable = false) // ⬅️ JPA: Columna no puede ser nula
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true) // ⬅️ JPA: No puede ser nulo Y no se puede repetir
    private String email;

    @Column(nullable = false)
    private String password; // (Más adelante la "hashearemos" con Spring Security)

    @Column(nullable = false)
    private String rol; // (Aquí guardaremos si es "HUESPED" o "ANFITRION")
}
