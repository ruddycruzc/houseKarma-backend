package com.housekarma.housekarma.experiencia.dto;

import com.housekarma.housekarma.experiencia.entity.ExperienciaAlquiler;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExperienciaAlquilerResponse(
        Long id,
        Long viviendaId,
        Long inquilinoId,
        Long caseroId,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        ExperienciaAlquiler.EstadoExperiencia estado,
        boolean confirmacionInquilino,
        boolean confirmacionCasero,
        LocalDateTime fechaRegistro) {
    public static ExperienciaAlquilerResponse desde(
            ExperienciaAlquiler experiencia) {
        return new ExperienciaAlquilerResponse(
                experiencia.getId(),
                experiencia.getVivienda().getId(),
                experiencia.getInquilino().getId(),
                experiencia.getCasero().getId(),
                experiencia.getFechaInicio(),
                experiencia.getFechaFin(),
                experiencia.getEstado(),
                experiencia.isConfirmacionInquilino(),
                experiencia.isConfirmacionCasero(),
                experiencia.getFechaRegistro());
    }
}