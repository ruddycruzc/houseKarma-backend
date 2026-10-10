package com.housekarma.housekarma.usuario.service;

public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(String email) {
        super("No se ha encontrado ningún usuario con el email: " + email);
    }
}