package com.housekarma.housekarma.experiencia.dto;

import com.housekarma.housekarma.experiencia.entity.ExperienciaAlquiler;
import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.vivienda.entity.Vivienda;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExperienciaAlquilerResponseTest {

    @Test
    void debeConvertirUnaExperienciaPendienteEnResponse() {
        ExperienciaAlquiler experiencia = crearExperiencia();

        ExperienciaAlquilerResponse response = ExperienciaAlquilerResponse.desde(experiencia);

        assertEquals(10L, response.viviendaId());
        assertEquals(1L, response.inquilinoId());
        assertEquals(2L, response.caseroId());
        assertEquals(LocalDate.of(2025, 1, 1), response.fechaInicio());
        assertEquals(LocalDate.of(2025, 12, 31), response.fechaFin());
        assertEquals(
                ExperienciaAlquiler.EstadoExperiencia.PENDIENTE_CONFIRMACION,
                response.estado());
        assertTrue(response.confirmacionInquilino());
        assertFalse(response.confirmacionCasero());
        assertEquals(
                LocalDateTime.of(2025, 1, 1, 10, 30),
                response.fechaRegistro());
    }

    @Test
    void debeConvertirUnaExperienciaConfirmadaEnResponse() {
        ExperienciaAlquiler experiencia = crearExperiencia();
        experiencia.setConfirmacionCasero(true);
        experiencia.setEstado(
                ExperienciaAlquiler.EstadoExperiencia.CONFIRMADA);

        ExperienciaAlquilerResponse response = ExperienciaAlquilerResponse.desde(experiencia);

        assertEquals(
                ExperienciaAlquiler.EstadoExperiencia.CONFIRMADA,
                response.estado());
        assertTrue(response.confirmacionInquilino());
        assertTrue(response.confirmacionCasero());
    }

    private ExperienciaAlquiler crearExperiencia() {
        Usuario inquilino = crearUsuario(
                1L, "inquilino@test.com", Rol.INQUILINO);
        Usuario casero = crearUsuario(
                2L, "casero@test.com", Rol.CASERO);

        Vivienda vivienda = new Vivienda() {
            @Override
            public Long getId() {
                return 10L;
            }
        };
        vivienda.setCasero(casero);

        ExperienciaAlquiler experiencia = new ExperienciaAlquiler() {
            @Override
            public Long getId() {
                return 100L;
            }
        };

        experiencia.setVivienda(vivienda);
        experiencia.setInquilino(inquilino);
        experiencia.setCasero(casero);
        experiencia.setFechaInicio(LocalDate.of(2025, 1, 1));
        experiencia.setFechaFin(LocalDate.of(2025, 12, 31));
        experiencia.setEstado(
                ExperienciaAlquiler.EstadoExperiencia.PENDIENTE_CONFIRMACION);
        experiencia.setConfirmacionInquilino(true);
        experiencia.setConfirmacionCasero(false);
        experiencia.setFechaRegistro(
                LocalDateTime.of(2025, 1, 1, 10, 30));

        return experiencia;
    }

    private Usuario crearUsuario(Long id, String email, Rol rol) {
        Usuario usuario = new Usuario() {
            @Override
            public Long getId() {
                return id;
            }
        };

        usuario.setNombre("Usuario");
        usuario.setApellidos("Prueba");
        usuario.setEmail(email);
        usuario.setPassword("password-cifrada");
        usuario.setRol(rol);
        usuario.setFechaRegistro(LocalDateTime.now());

        return usuario;
    }

}