
package com.housekarma.housekarma.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRegistroRequest(

        @NotBlank
        @Size(max = 100)
        String nombre,

        @NotBlank
        @Size(max = 150)
        String apellidos,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(min = 8, max = 72)
        String password,

        @Size(max = 20)
        String telefono

) {
}
