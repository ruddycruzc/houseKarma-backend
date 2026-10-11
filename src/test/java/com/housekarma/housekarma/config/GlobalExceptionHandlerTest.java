package com.housekarma.housekarma.config;

import com.housekarma.housekarma.auth.exception.CredencialesInvalidasException;
import com.housekarma.housekarma.usuario.service.EmailYaRegistradoException;
import com.housekarma.housekarma.usuario.service.UsuarioNoEncontradoException;
import com.housekarma.housekarma.vivienda.service.ViviendaDuplicadaException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void debeDevolver409CuandoElEmailYaEstaRegistrado() {
        EmailYaRegistradoException exception = mock(EmailYaRegistradoException.class);

        ProblemDetail response = handler.handleEmailYaRegistrado(exception);

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals("Email duplicado", response.getTitle());
    }

    @Test
    void debeDevolver400CuandoHayErroresDeValidacion() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);

        when(exception.getBindingResult()).thenReturn(
                new org.springframework.validation.BeanPropertyBindingResult(
                        new Object(), "objeto"));

        ProblemDetail response = handler.handleErroresDeValidacion(exception);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Error de validación", response.getTitle());
    }

    @Test
    void debeDevolver401CuandoLasCredencialesSonInvalidas() {
        CredencialesInvalidasException exception = mock(CredencialesInvalidasException.class);

        ProblemDetail response = handler.handleCredencialesInvalidas(exception);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertEquals("Credenciales inválidas", response.getTitle());
    }

    @Test
    void debeDevolver404CuandoElUsuarioNoExiste() {
        UsuarioNoEncontradoException exception = mock(UsuarioNoEncontradoException.class);

        when(exception.getMessage()).thenReturn("Usuario inexistente");

        ProblemDetail response = handler.handleUsuarioNoEncontrado(exception);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals("Usuario no encontrado", response.getTitle());
        assertEquals("Usuario inexistente", response.getDetail());
    }

    @Test
    void debeDevolver409CuandoLaViviendaEstaDuplicada() {
        ViviendaDuplicadaException exception = new ViviendaDuplicadaException(
                "La vivienda con esa dirección ya existe");

        ProblemDetail response = handler.handleViviendaDuplicada(exception);

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals("Vivienda duplicada", response.getTitle());
    }

    @Test
    void debeDevolver400CuandoHayUnArgumentoInvalido() {
        IllegalArgumentException exception = new IllegalArgumentException("La dirección es obligatoria");

        ProblemDetail response = handler.handleArgumentoInvalido(exception);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Datos inválidos", response.getTitle());
        assertEquals("La dirección es obligatoria", response.getDetail());
    }
}