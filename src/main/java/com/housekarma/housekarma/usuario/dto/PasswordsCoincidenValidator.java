
package com.housekarma.housekarma.usuario.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class PasswordsCoincidenValidator
        implements ConstraintValidator<PasswordsCoinciden, UsuarioRegistroRequest> {

    @Override
    public boolean isValid(
            UsuarioRegistroRequest request,
            ConstraintValidatorContext context
    ) {
        if (request == null) {
            return true;
        }

        return Objects.equals(
                request.password(),
                request.confirmarPassword()
        );
    }
}
