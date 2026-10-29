
package com.housekarma.housekarma.valoracion.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ValoracionCriterioRequest(

        @NotBlank String criterio,

        @NotNull @Min(1) @Max(5) Integer puntuacion

) {
}
