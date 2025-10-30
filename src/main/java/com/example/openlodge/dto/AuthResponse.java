package com.example.openlodge.dto;

import lombok.Data;

@Data // Lombok nos da getters y setters
public class AuthResponse {
    private final String token;
}