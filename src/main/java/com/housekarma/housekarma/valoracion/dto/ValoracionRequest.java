package com.housekarma.housekarma.valoracion.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ValoracionRequest(

        @NotEmpty @Size(min = 5, max = 5) List<@Valid ValoracionCriterioRequest> criterios,

        @Size(max = 1000) String comentario

) {
}
