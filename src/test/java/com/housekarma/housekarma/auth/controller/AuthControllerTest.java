package com.housekarma.housekarma.auth.controller;

import com.housekarma.housekarma.auth.dto.AuthResponse;
import com.housekarma.housekarma.auth.dto.LoginRequest;
import com.housekarma.housekarma.auth.exception.CredencialesInvalidasException;
import com.housekarma.housekarma.auth.service.AuthService;
import com.housekarma.housekarma.config.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new AuthController(authService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void debePermitirIniciarSesionConCredencialesCorrectas()
            throws Exception {

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new AuthResponse(
                        "token-de-prueba",
                        "ana@example.com",
                        "INQUILINO"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "ana@example.com",
                          "password": "ClaveSegura123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token")
                        .value("token-de-prueba"))
                .andExpect(jsonPath("$.email")
                        .value("ana@example.com"))
                .andExpect(jsonPath("$.rol")
                        .value("INQUILINO"))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void debeRechazarCredencialesIncorrectas() throws Exception {

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new CredencialesInvalidasException());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "ana@example.com",
                          "password": "Incorrecta123"
                        }
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRechazarEmailInvalido() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "esto-no-es-un-email",
                            "password": "ClaveSegura123"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRechazarPasswordVacia() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "ana@example.com",
                            "password": ""
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

}