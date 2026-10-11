package com.housekarma.housekarma.vivienda.dto;

import com.housekarma.housekarma.vivienda.entity.Vivienda;
import java.time.LocalDateTime;

public record ViviendaResponse(
        Long id,
        String direccion,
        String numero,
        String complemento,
        String localidad,
        String codigoPostal,
        String provincia,
        LocalDateTime fechaRegistro
) {
    public static ViviendaResponse fromEntity(Vivienda vivienda) {
        return new ViviendaResponse(
                vivienda.getId(),
                vivienda.getDireccion(),
                vivienda.getNumero(),
                vivienda.getComplemento(),
                vivienda.getLocalidad(),
                vivienda.getCodigoPostal(),
                vivienda.getProvincia(),
                vivienda.getFechaRegistro()
        );
    }
}