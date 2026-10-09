
package com.housekarma.housekarma.usuario.service;

public class EmailYaRegistradoException extends RuntimeException {

    public EmailYaRegistradoException(String email) {
        super("El email ya está registrado: " + email);
    }
}
