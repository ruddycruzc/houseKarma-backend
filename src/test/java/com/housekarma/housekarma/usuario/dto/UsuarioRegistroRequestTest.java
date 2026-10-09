
package com.housekarma.housekarma.usuario.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioRegistroRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void debeRechazarEmailInvalidoYContrasenaCorta() {
        UsuarioRegistroRequest request = new UsuarioRegistroRequest(
                "Ana",
                "Garcia",
                "correo-invalido",
                "correo-invalido",
                "123",
                "123",
                null);

        var errores = validator.validate(request);

        assertFalse(errores.isEmpty());
        assertTrue(errores.stream().anyMatch(
                error -> error.getPropertyPath().toString().equals("email")));
        assertTrue(errores.stream().anyMatch(
                error -> error.getPropertyPath().toString().equals("password")));
    }

    @Test
    void debeAceptarDatosValidos() {
        UsuarioRegistroRequest request = new UsuarioRegistroRequest(
                "Ana",
                "Garcia",
                "ana@example.com",
                "ana@example.com",
                "Password123",
                "Password123",
                "600123456");

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void debeRechazarContrasenasQueNoCoinciden() {
        UsuarioRegistroRequest request = new UsuarioRegistroRequest(
                "Ana",
                "Garcia",
                "ana@example.com",
                "ana@example.com",
                "Password123",
                "Password456",
                "600123456");

        var errores = validator.validate(request);

        assertTrue(errores.stream().anyMatch(
                error -> error.getMessage()
                        .equals("Las contraseñas no coinciden")));
    }

    @Test
    void debeRechazarEmailsQueNoCoinciden() {
        UsuarioRegistroRequest request = new UsuarioRegistroRequest(
                "Ana",
                "Garcia",
                "ana@example.com",
                "otro@example.com",
                "Password123",
                "Password123",
                "600123456");

        var errores = validator.validate(request);

        assertTrue(errores.stream().anyMatch(
                error -> error.getMessage().equals("Los emails no coinciden")));
    }

}
