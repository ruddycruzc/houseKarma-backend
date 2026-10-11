
package com.housekarma.housekarma.config;

import com.housekarma.housekarma.usuario.service.EmailYaRegistradoException;
import com.housekarma.housekarma.auth.exception.CredencialesInvalidasException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.housekarma.housekarma.usuario.service.UsuarioNoEncontradoException;
import com.housekarma.housekarma.vivienda.service.ViviendaDuplicadaException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(EmailYaRegistradoException.class)
        public ProblemDetail handleEmailYaRegistrado(
                        EmailYaRegistradoException exception) {
                ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                                HttpStatus.CONFLICT,
                                "El email ya está registrado.");

                problem.setTitle("Email duplicado");

                return problem;
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ProblemDetail handleErroresDeValidacion(
                        MethodArgumentNotValidException exception) {
                Map<String, String> errores = new LinkedHashMap<>();

                exception.getBindingResult().getFieldErrors()
                                .forEach(error -> errores.putIfAbsent(
                                                error.getField(),
                                                error.getDefaultMessage()));

                exception.getBindingResult().getGlobalErrors()
                                .forEach(error -> errores.putIfAbsent(
                                                "_global",
                                                error.getDefaultMessage()));

                ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                                HttpStatus.BAD_REQUEST,
                                "Uno o varios campos no son válidos.");

                problem.setTitle("Error de validación");
                problem.setProperty("errors", errores);

                return problem;
        }

        @ExceptionHandler(CredencialesInvalidasException.class)
        public ProblemDetail handleCredencialesInvalidas(
                        CredencialesInvalidasException exception) {

                ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                                HttpStatus.UNAUTHORIZED,
                                "Email o contraseña incorrectos.");

                problem.setTitle("Credenciales inválidas");

                return problem;
        }

        @ExceptionHandler(UsuarioNoEncontradoException.class)
        public ProblemDetail handleUsuarioNoEncontrado(
                        UsuarioNoEncontradoException exception) {

                ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                                HttpStatus.NOT_FOUND,
                                exception.getMessage());

                problem.setTitle("Usuario no encontrado");

                return problem;
        }

        @ExceptionHandler(ViviendaDuplicadaException.class)
        public ProblemDetail handleViviendaDuplicada(
                        ViviendaDuplicadaException exception) {

                ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                                HttpStatus.CONFLICT,
                                exception.getMessage());

                problem.setTitle("Vivienda duplicada");

                return problem;
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ProblemDetail handleArgumentoInvalido(
                        IllegalArgumentException exception) {

                ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                                HttpStatus.BAD_REQUEST,
                                exception.getMessage());

                problem.setTitle("Datos inválidos");

                return problem;
        }
}
