package com.housekarma.housekarma.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void debePermitirAccesoAnonimoAlLogin() throws Exception {
        var resultado = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "ana@example.com",
                                    "password": "ClaveSegura123"
                                }
                                """))
                .andReturn();

        int httpStatus = resultado.getResponse().getStatus();

        assertNotEquals(
                403,
                httpStatus,
                "El login público no debería ser bloqueado por la configuración de seguridad"
        );
    }

    @Test
void debePermitirPreflightCorsDesdeAngular() throws Exception {
    mockMvc.perform(options("/api/viviendas")
                    .header("Origin", "http://localhost:4200")
                    .header("Access-Control-Request-Method", "GET")
                    .header("Access-Control-Request-Headers", "authorization"))
            .andExpect(status().isOk())
            .andExpect(header().string(
                    "Access-Control-Allow-Origin",
                    "http://localhost:4200"))
            .andExpect(header().string(
                    "Access-Control-Allow-Methods",
                    org.hamcrest.Matchers.containsString("GET")))
            .andExpect(header().string(
                    "Access-Control-Allow-Headers",
                    org.hamcrest.Matchers.containsString("authorization")));
}
}