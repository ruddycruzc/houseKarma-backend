
package com.housekarma.housekarma.config;

import com.housekarma.housekarma.usuario.service.EmailYaRegistradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailYaRegistradoException.class)
    public ProblemDetail handleEmailYaRegistrado(
            EmailYaRegistradoException exception
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "El email ya está registrado."
        );

        problem.setTitle("Email duplicado");

        return problem;
    }
}
