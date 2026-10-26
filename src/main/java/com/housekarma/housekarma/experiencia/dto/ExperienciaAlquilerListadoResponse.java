package com.housekarma.housekarma.experiencia.dto;

import com.housekarma.housekarma.experiencia.entity.ExperienciaAlquiler;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExperienciaAlquilerListadoResponse(
        Long id,
        Long viviendaId,
        String viviendaDireccion,
        String viviendaNumero,
        String viviendaComplemento,
        String viviendaLocalidad,
        String viviendaCodigoPostal,
        String viviendaProvincia,
        Long inquilinoId,
        String inquilinoNombre,
        String inquilinoApellidos,
        Long caseroId,
        String caseroNombre,
        String caseroApellidos,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        ExperienciaAlquiler.EstadoExperiencia estado,
        boolean confirmacionInquilino,
        boolean confirmacionCasero,
        LocalDateTime fechaRegistro
) {
    public static ExperienciaAlquilerListadoResponse desde(
            ExperienciaAlquiler experiencia) {

        var vivienda = experiencia.getVivienda();
        var inquilino = experiencia.getInquilino();
        var casero = experiencia.getCasero();

        return new ExperienciaAlquilerListadoResponse(
                experiencia.getId(),
                vivienda.getId(),
                vivienda.getDireccion(),
                vivienda.getNumero(),
                vivienda.getComplemento(),
                vivienda.getLocalidad(),
                vivienda.getCodigoPostal(),
                vivienda.getProvincia(),
                inquilino.getId(),
                inquilino.getNombre(),
                inquilino.getApellidos(),
                casero.getId(),
                casero.getNombre(),
                casero.getApellidos(),
                experiencia.getFechaInicio(),
                experiencia.getFechaFin(),
                experiencia.getEstado(),
                experiencia.isConfirmacionInquilino(),
                experiencia.isConfirmacionCasero(),
                experiencia.getFechaRegistro()
        );
    }
}