package com.housekarma.housekarma.usuario.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsuarioTest {

    @Test
    void deberiaCrearUsuarioConDatosBasicos() {
        Usuario usuario = new Usuario();

        usuario.setNombre("Ruddy");
        usuario.setApellidos("Cruz");
        usuario.setEmail("ruddy@example.com");

        assertEquals("Ruddy", usuario.getNombre());
        assertEquals("Cruz", usuario.getApellidos());
        assertEquals("ruddy@example.com", usuario.getEmail());
    }
}