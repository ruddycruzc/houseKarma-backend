package com.housekarma.housekarma.auth.service;

import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET_KEY = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    private JwtService jwtService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET_KEY, 3_600_000L);

        usuario = new Usuario();
        usuario.setEmail("ana@example.com");
        usuario.setRol(Rol.INQUILINO);
    }

    @Test
    void debeGenerarUnTokenValido() {
        String token = jwtService.generarToken(usuario);

        assertTrue(jwtService.esTokenValido(token));
    }

    @Test
    void debeIncluirElEmailComoIdentidadDelToken() {
        String token = jwtService.generarToken(usuario);

        assertEquals(
                "ana@example.com",
                jwtService.extraerEmail(token));
    }

    @Test
    void debeRechazarUnTokenManipulado() {
        String token = jwtService.generarToken(usuario);

        char ultimoCaracter = token.charAt(token.length() - 1);
        char caracterAlternativo = ultimoCaracter == 'a' ? 'b' : 'a';

        String tokenManipulado = token.substring(0, token.length() - 1)
                + caracterAlternativo;

        assertFalse(jwtService.esTokenValido(tokenManipulado));
    }

    @Test
    void debeRechazarUnTokenCaducado() throws InterruptedException {
        JwtService servicioConExpiracionCorta = new JwtService(SECRET_KEY, 1L);

        String token = servicioConExpiracionCorta.generarToken(usuario);

        Thread.sleep(20);

        assertFalse(servicioConExpiracionCorta.esTokenValido(token));
    }
}