
package com.housekarma.housekarma.usuario.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioBusquedaResponseTest {

    @Test
    void debeCrearRespuestaConTodosLosCampos() {
        UsuarioBusquedaResponse respuesta = new UsuarioBusquedaResponse(
                1L,
                "María",
                "Fernández López",
                "https://ejemplo.com/avatar.png",
                "CASERO"
        );

        assertEquals(1L, respuesta.id());
        assertEquals("María", respuesta.nombre());
        assertEquals("Fernández López", respuesta.apellidos());
        assertEquals("https://ejemplo.com/avatar.png", respuesta.avatar());
        assertEquals("CASERO", respuesta.rol());
    }

    @Test
    void debePermitirCamposNulosSiNoHayValidacionesDeclaradas() {
        UsuarioBusquedaResponse respuesta = new UsuarioBusquedaResponse(
                1L,
                "María",
                "Fernández López",
                null,
                "CASERO"
        );

        assertNull(respuesta.avatar());
        assertEquals(1L, respuesta.id());
    }

    @Test
    void debeCompararRespuestasPorSusValores() {
        UsuarioBusquedaResponse primera = new UsuarioBusquedaResponse(
                1L, "María", "Fernández", null, "CASERO"
        );

        UsuarioBusquedaResponse segunda = new UsuarioBusquedaResponse(
                1L, "María", "Fernández", null, "CASERO"
        );

        assertEquals(primera, segunda);
        assertEquals(primera.hashCode(), segunda.hashCode());
    }

    @Test
    void debeDetectarRespuestasConValoresDiferentes() {
        UsuarioBusquedaResponse primera = new UsuarioBusquedaResponse(
                1L, "María", "Fernández", null, "CASERO"
        );

        UsuarioBusquedaResponse segunda = new UsuarioBusquedaResponse(
                2L, "María", "Fernández", null, "CASERO"
        );

        assertNotEquals(primera, segunda);
    }

    @Test
    void debeGenerarRepresentacionEnTexto() {
        UsuarioBusquedaResponse respuesta = new UsuarioBusquedaResponse(
                1L, "María", "Fernández", null, "CASERO"
        );

        String resultado = respuesta.toString();

        assertTrue(resultado.contains("María"));
        assertTrue(resultado.contains("Fernández"));
        assertTrue(resultado.contains("CASERO"));
    }
}
