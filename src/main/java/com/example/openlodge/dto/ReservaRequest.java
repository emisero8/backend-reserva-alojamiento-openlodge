package com.example.openlodge.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ReservaRequest {
    private Long propiedadId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Double precioTotal;
    private String notas;
}
