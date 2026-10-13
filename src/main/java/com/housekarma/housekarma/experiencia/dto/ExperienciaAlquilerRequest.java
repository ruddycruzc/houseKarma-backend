package com.housekarma.housekarma.experiencia.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record ExperienciaAlquilerRequest(
        @NotNull
        @Positive
        Long viviendaId,

        @Positive
        Long inquilinoId,

        @NotNull
        LocalDate fechaInicio,

        LocalDate fechaFin
) {
}