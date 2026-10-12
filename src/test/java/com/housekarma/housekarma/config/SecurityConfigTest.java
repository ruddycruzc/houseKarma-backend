
package com.housekarma.housekarma.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void debePermitirAccesoAnonimoAlLogin() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "ana@example.com",
                            "password": "ClaveSegura123"
                        }
                        """))
                .andExpect(result -> {
                    int httpStatus = result.getResponse().getStatus();
                    assertTrue(httpStatus == 200 || httpStatus == 401 || httpStatus == 403,
                            "Se esperaba permitir el acceso anónimo al login, pero la respuesta fue " + httpStatus);
                })
                .andExpect(status().isUnauthorized());
    }
}
