package com.housekarma.housekarma.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioPerfilActualizarRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 150, message = "Los apellidos no pueden superar los 150 caracteres")
        String apellidos,

        @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
        String telefono,

        @Size(max = 500, message = "La URL del avatar no puede superar los 500 caracteres")
        String avatar
) {}