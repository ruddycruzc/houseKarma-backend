package com.housekarma.housekarma.experiencia.dto;

import com.housekarma.housekarma.experiencia.entity.ExperienciaAlquiler;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExperienciaAlquilerPendienteResponse(
        Long id,
        String inquilinoNombre,
        String inquilinoApellidos,
        Long viviendaId,
        String viviendaDireccion,
        String viviendaNumero,
        String viviendaComplemento,
        String viviendaLocalidad,
        String viviendaCodigoPostal,
        String viviendaProvincia,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        ExperienciaAlquiler.EstadoExperiencia estado,
        boolean confirmacionInquilino,
        boolean confirmacionCasero,
        LocalDateTime fechaRegistro
) {
    public static ExperienciaAlquilerPendienteResponse desde(
            ExperienciaAlquiler experiencia) {

        var inquilino = experiencia.getInquilino();
        var vivienda = experiencia.getVivienda();

        return new ExperienciaAlquilerPendienteResponse(
                experiencia.getId(),
                inquilino.getNombre(),
                inquilino.getApellidos(),
                vivienda.getId(),
                vivienda.getDireccion(),
                vivienda.getNumero(),
                vivienda.getComplemento(),
                vivienda.getLocalidad(),
                vivienda.getCodigoPostal(),
                vivienda.getProvincia(),
                experiencia.getFechaInicio(),
                experiencia.getFechaFin(),
                experiencia.getEstado(),
                experiencia.isConfirmacionInquilino(),
                experiencia.isConfirmacionCasero(),
                experiencia.getFechaRegistro()
        );
    }
}