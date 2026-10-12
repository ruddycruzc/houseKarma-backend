
package com.housekarma.housekarma.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@PasswordsCoinciden
@EmailsCoinciden
public record UsuarioRegistroRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 150, message = "Los apellidos no pueden superar los 150 caracteres")
        String apellidos,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 255, message = "El email no puede superar los 255 caracteres")
        String email,

        @NotBlank(message = "Debes confirmar el email")
        @Email(message = "El email de confirmación no tiene un formato válido")
        @Size(max = 255)
        String confirmarEmail,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72,
        message = "La contraseña debe tener entre 8 y 72 caracteres")
        String password,

        @NotBlank(message = "Debes confirmar la contraseña")
        @Size(min = 8, max = 72,
                message = "La confirmación debe coincidir")
        String confirmarPassword,

       @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
        String telefono,

        @NotBlank(message = "Debes seleccionar un tipo de usuario")
        @Pattern(
        regexp = "INQUILINO|CASERO",
        message = "El rol debe ser INQUILINO o CASERO"
        )
        String rol
        ) {
        }
