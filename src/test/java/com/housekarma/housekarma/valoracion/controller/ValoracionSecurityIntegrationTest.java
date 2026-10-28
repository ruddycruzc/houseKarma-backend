package com.housekarma.housekarma.valoracion.controller;

import com.housekarma.housekarma.auth.service.JwtService;
import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;
import com.housekarma.housekarma.valoracion.dto.ValoracionRequest;
import com.housekarma.housekarma.valoracion.service.ValoracionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ValoracionSecurityIntegrationTest {

    private static final String ENDPOINT = "/api/valoraciones/experiencias/10";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private ValoracionService valoracionService;

    @Test
    void debeRechazarRegistroSinAutenticacion() throws Exception {
        mockMvc.perform(post("/api/valoraciones/experiencias/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "puntuacion": 5,
                            "comentario": "Buena experiencia"
                        }
                        """))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(valoracionService);
    }

    @Test
    void debeDenegarRegistroAUnAdministrador() throws Exception {
        configurarUsuarioAutenticado(
                "token-admin",
                "admin@test.com",
                Rol.ADMIN);

        mockMvc.perform(post(ENDPOINT)
                .header("Authorization", "Bearer token-admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "puntuacion": 5,
                            "comentario": "Buena experiencia"
                        }
                        """))
                .andExpect(status().isForbidden());

        verifyNoInteractions(valoracionService);
    }

    @Test
    void debePermitirLlegarAlServicioAUnInquilinoAutenticado()
            throws Exception {
        configurarUsuarioAutenticado(
                "token-inquilino",
                "inquilino@test.com",
                Rol.INQUILINO);

        mockMvc.perform(post(ENDPOINT)
                .header("Authorization", "Bearer token-inquilino")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "puntuacion": 5,
                            "comentario": "Buena experiencia"
                        }
                        """))
                .andExpect(status().isCreated());

        verify(valoracionService).registrar(
                eq("inquilino@test.com"),
                eq(10L),
                any(ValoracionRequest.class));
    }

    @Test
    void debeRechazarPuntuacionFueraDeRango() throws Exception {
        configurarUsuarioAutenticado(
                "token-inquilino",
                "inquilino@test.com",
                Rol.INQUILINO);

        mockMvc.perform(post(ENDPOINT)
                .header("Authorization", "Bearer token-inquilino")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "puntuacion": 6,
                            "comentario": "Buena experiencia"
                        }
                        """))
                .andExpect(status().isBadRequest());

        verify(valoracionService, never())
                .registrar(anyString(), anyLong(), any(ValoracionRequest.class));
    }

    private void configurarUsuarioAutenticado(
            String token,
            String email,
            Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setRol(rol);

        when(jwtService.esTokenValido(token)).thenReturn(true);
        when(jwtService.extraerEmail(token)).thenReturn(email);
        when(usuarioRepository.findByEmail(email))
                .thenReturn(Optional.of(usuario));
    }
}