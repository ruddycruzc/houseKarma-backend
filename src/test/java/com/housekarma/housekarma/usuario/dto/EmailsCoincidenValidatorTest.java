package com.housekarma.housekarma.usuario.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailsCoincidenValidatorTest {

    private final EmailsCoincidenValidator validator = new EmailsCoincidenValidator();

    @Test
    void acceptsIdenticalEmails() {
        assertTrue(validator.isValid(registration("alex@example.com", "alex@example.com"), null));
    }

    @Test
    void rejectsEmailConfirmationThatDiffersOnlyByCase() {
        assertFalse(validator.isValid(registration("Alex@example.com", "alex@example.com"), null));
    }

    private UsuarioRegistroRequest registration(String email, String confirmation) {
        return new UsuarioRegistroRequest(
                "Alex",
                "Example",
                email,
                confirmation,
                "Password123",
                "Password123",
                null,
                "INQUILINO");
    }
}
