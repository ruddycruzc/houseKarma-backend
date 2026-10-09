package com.housekarma.housekarma.auth.filter;

import com.housekarma.housekarma.auth.service.JwtService;
import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

class JwtAuthenticationFilterTest {

    private static final String SECRET_KEY =
            "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET_KEY, 3_600_000L);
        SecurityContextHolder.clearContext();
    }

    @Test
    void debeContinuarSinAutenticarCuandoNoHayToken() throws Exception {
        // Este test se completará cuando implementemos el filtro.
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void debeRechazarUnTokenManipulado() {
        String tokenManipulado = "token.invalido.firma";

        assertEquals(false, jwtService.esTokenValido(tokenManipulado));
    }
}
