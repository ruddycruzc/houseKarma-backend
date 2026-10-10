package com.housekarma.housekarma.usuario.controller;

import com.housekarma.housekarma.auth.service.JwtService;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;
import com.housekarma.housekarma.usuario.dto.UsuarioRegistroRequest;
import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.usuario.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioSecurityIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private UsuarioService usuarioService;

        @MockitoBean
        private JwtService jwtService;

        @MockitoBean
        private UsuarioRepository usuarioRepository;

        @Test
        void debePermitirRegistroSinAutenticacion() throws Exception {
                Usuario usuario = new Usuario();
                usuario.setNombre("Ana");
                usuario.setApellidos("García");
                usuario.setEmail("ana@example.com");
                usuario.setRol(Rol.INQUILINO);

                when(usuarioService.registrarUsuario(any(UsuarioRegistroRequest.class)))
                                .thenReturn(usuario);

                mockMvc.perform(post("/api/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "nombre": "Ana",
                                                  "apellidos": "García",
                                                  "email": "ana@example.com",
                                                  "confirmarEmail": "ana@example.com",
                                                  "password": "ClaveSegura123",
                                                  "confirmarPassword": "ClaveSegura123",
                                                  "telefono": "600123456"
                                                }
                                                """))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.email").value("ana@example.com"))
                                .andExpect(jsonPath("$.password").doesNotExist());
        }

        @Test
        void debeRechazarRegistroConDatosInvalidos() throws Exception {
                mockMvc.perform(post("/api/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "nombre": "",
                                                  "email": "correo-invalido"
                                                }
                                                """))
                                .andExpect(status().isBadRequest());

                verify(usuarioService, never())
                                .registrarUsuario(any(UsuarioRegistroRequest.class));
        }

        @Test
        void debeProtegerLasRutasQueNoSonPublicas() throws Exception {
                mockMvc.perform(get("/api/usuarios"))
                                .andExpect(status().is4xxClientError());
        }

        @Test
        void debePermitirAccesoConTokenJwtValido() throws Exception {
                Usuario usuario = new Usuario();
                usuario.setNombre("Ana");
                usuario.setApellidos("García");
                usuario.setEmail("ana@example.com");
                usuario.setRol(Rol.INQUILINO);

                when(jwtService.esTokenValido("token-valido"))
                                .thenReturn(true);

                when(jwtService.extraerEmail("token-valido"))
                                .thenReturn("ana@example.com");

                when(usuarioRepository.findByEmail("ana@example.com"))
                                .thenReturn(Optional.of(usuario));

                mockMvc.perform(get("/api/test/protected")
                                .header("Authorization", "Bearer token-valido"))
                                .andExpect(status().isOk())
                                .andExpect(
                                                org.springframework.test.web.servlet.result.MockMvcResultMatchers
                                                                .content().string("Acceso autorizado"));
        }

}
