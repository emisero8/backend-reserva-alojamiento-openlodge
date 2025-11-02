package com.example.openlodge.dto;

import lombok.Data;

@Data // Lombok nos da getters y setters
public class AuthResponse {
    private final String token;
    private final String rol;
    private final String nombre;
    private final String apellido;
    private final String email;

    public AuthResponse(String token, String rol, String nombre, String apellido, String email) {
        this.token = token;
        this.rol = rol;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }
}