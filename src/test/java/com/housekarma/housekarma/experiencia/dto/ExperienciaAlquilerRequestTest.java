package com.housekarma.housekarma.experiencia.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExperienciaAlquilerRequestTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void debeAceptarUnaSolicitudValida() {
        ExperienciaAlquilerRequest request = new ExperienciaAlquilerRequest(
                1L,
                null,
                LocalDate.of(2025, 1, 1),
                null
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void debeRechazarUnaViviendaNula() {
        ExperienciaAlquilerRequest request = new ExperienciaAlquilerRequest(
                null,
                null,
                LocalDate.of(2025, 1, 1),
                null
        );

        Set<?> violations = validator.validate(request);

        assertEquals(1, violations.size());
    }

    @Test
    void debeRechazarUnIdentificadorDeViviendaNoPositivo() {
        ExperienciaAlquilerRequest request = new ExperienciaAlquilerRequest(
                0L,
                null,
                LocalDate.of(2025, 1, 1),
                null
        );

        assertEquals(1, validator.validate(request).size());
    }

    @Test
    void debeRechazarUnaFechaInicioNula() {
        ExperienciaAlquilerRequest request = new ExperienciaAlquilerRequest(
                1L,
                null,
                null,
                null
        );

        assertEquals(1, validator.validate(request).size());
    }

    @Test
    void debeRechazarUnInquilinoConIdentificadorNegativo() {
        ExperienciaAlquilerRequest request = new ExperienciaAlquilerRequest(
                1L,
                -2L,
                LocalDate.of(2025, 1, 1),
                null
        );

        assertEquals(1, validator.validate(request).size());
    }

    @Test
    void debeAceptarUnInquilinoNuloPorquePuedeIniciarElInquilinoAutenticado() {
        ExperienciaAlquilerRequest request = new ExperienciaAlquilerRequest(
                1L,
                null,
                LocalDate.of(2025, 1, 1),
                null
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void debeAceptarUnaFechaFinNula() {
        ExperienciaAlquilerRequest request = new ExperienciaAlquilerRequest(
                1L,
                null,
                LocalDate.of(2025, 1, 1),
                null
        );

        assertTrue(validator.validate(request).isEmpty());
    }
}