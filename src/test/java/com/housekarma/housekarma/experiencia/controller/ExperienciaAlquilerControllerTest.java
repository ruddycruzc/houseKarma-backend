package com.housekarma.housekarma.experiencia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.housekarma.housekarma.experiencia.dto.ExperienciaAlquilerRequest;
import com.housekarma.housekarma.experiencia.entity.ExperienciaAlquiler;
import com.housekarma.housekarma.experiencia.service.ExperienciaAlquilerService;
import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.vivienda.entity.Vivienda;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.housekarma.housekarma.auth.service.JwtService;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(ExperienciaAlquilerController.class)
class ExperienciaAlquilerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();

    @MockitoBean
    private ExperienciaAlquilerService experienciaService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    @WithMockUser(username = "inquilino@test.com", roles = "INQUILINO")
    void debeRegistrarExperienciaYDevolverCreated() throws Exception {
        ExperienciaAlquilerRequest request = new ExperienciaAlquilerRequest(
                10L,
                null,
                LocalDate.of(2025, 1, 1),
                null);

        when(experienciaService.registrarExperiencia(
                eq("inquilino@test.com"),
                eq(10L),
                isNull(),
                eq(LocalDate.of(2025, 1, 1)),
                isNull())).thenReturn(crearExperiencia());

        mockMvc.perform(post("/api/experiencias")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.viviendaId").value(10))
                .andExpect(jsonPath("$.inquilinoId").value(1))
                .andExpect(jsonPath("$.caseroId").value(2))
                .andExpect(jsonPath("$.estado")
                        .value("PENDIENTE_CONFIRMACION"));

        verify(experienciaService).registrarExperiencia(
                eq("inquilino@test.com"),
                eq(10L),
                isNull(),
                eq(LocalDate.of(2025, 1, 1)),
                isNull());
    }

    @Test
    @WithMockUser(username = "casero@test.com", roles = "CASERO")
    void debeConfirmarExperienciaYDevolverOk() throws Exception {
        ExperienciaAlquiler experiencia = crearExperiencia();
        experiencia.setConfirmacionCasero(true);
        experiencia.setEstado(
                ExperienciaAlquiler.EstadoExperiencia.CONFIRMADA);

        when(experienciaService.confirmarExperiencia(
                "casero@test.com", 100L))
                .thenReturn(experiencia);

        mockMvc.perform(patch("/api/experiencias/100/confirmacion")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmacionCasero").value(true))
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));

        verify(experienciaService).confirmarExperiencia(
                "casero@test.com", 100L);
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