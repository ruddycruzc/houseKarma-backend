
package com.housekarma.housekarma.usuario.dto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class EmailsCoincidenValidator
        implements ConstraintValidator<EmailsCoinciden, UsuarioRegistroRequest> {

    @Override
    public boolean isValid(
            UsuarioRegistroRequest request,
            ConstraintValidatorContext context
    ) {
        if (request == null) {
            return true;
        }

        return Objects.equals(
                request.email(),
                request.confirmarEmail()
        );
    }
}
