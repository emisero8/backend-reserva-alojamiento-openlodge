package com.example.openlodge.dto;

import lombok.Data;

@Data // Lombok nos da getters y setters
public class LoginRequest {
    private String email;
    private String password;
}