package com.housekarma.housekarma.vivienda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ViviendaRequest(

        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 255)
        String direccion,

        @NotBlank(message = "El número es obligatorio")
        @Size(max = 20)
        String numero,

        @Size(max = 100)
        String complemento,

        @NotBlank(message = "La localidad es obligatoria")
        @Size(max = 100)
        String localidad,

        @NotBlank(message = "El código postal es obligatorio")
        @Pattern(
                regexp = "\\d{5}",
                message = "El código postal debe tener 5 dígitos"
        )
        String codigoPostal,

        @NotBlank(message = "La provincia es obligatoria")
        @Size(max = 100)
        String provincia
) {
}