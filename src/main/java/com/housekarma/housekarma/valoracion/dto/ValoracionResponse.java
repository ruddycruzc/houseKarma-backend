package com.housekarma.housekarma.valoracion.dto;

import java.time.LocalDateTime;

public record ValoracionResponse(
    Long id,
    Long experienciaId,
    Long autorId,
    String autorNombre,
    Long destinatarioId,
    String destinatarioNombre,
    Integer puntuacion,
    String comentario,
    LocalDateTime fechaRegistro
) {}
