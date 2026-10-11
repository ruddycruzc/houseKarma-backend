package com.housekarma.housekarma.vivienda.controller;

import com.housekarma.housekarma.vivienda.dto.ViviendaRequest;
import com.housekarma.housekarma.vivienda.entity.Vivienda;
import com.housekarma.housekarma.vivienda.service.ViviendaDuplicadaException;
import com.housekarma.housekarma.vivienda.service.ViviendaService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import com.housekarma.housekarma.auth.service.JwtService;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.util.ReflectionTestUtils;

@WebMvcTest(ViviendaController.class)
class ViviendaControllerTest {

    private String convertirAJson(Object objeto) throws Exception {
        return Jackson2ObjectMapperBuilder.json()
                .build()
                .writeValueAsString(objeto);
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ViviendaService viviendaService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void debeRegistrarViviendaYDevolver201() throws Exception {
        Vivienda vivienda = crearVivienda();

        when(viviendaService.registrar(any(Vivienda.class)))
                .thenReturn(vivienda);

        ViviendaRequest request = new ViviendaRequest(
                "Calle Corrida", "10", null,
                "Gijón", "33206", "Asturias");

        mockMvc.perform(post("/api/viviendas")
                .with(user("test"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertirAJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.direccion").value("Calle Corrida"))
                .andExpect(jsonPath("$.localidad").value("Gijón"))
                .andExpect(jsonPath("$.codigoPostal").value("33206"));
    }

    @Test
    void debeRechazarViviendaConDireccionVacia() throws Exception {
        ViviendaRequest request = new ViviendaRequest(
                " ", "10", null,
                "Gijón", "33206", "Asturias");

        mockMvc.perform(post("/api/viviendas")
                .with(user("test"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertirAJson(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRechazarCodigoPostalInvalido() throws Exception {
        ViviendaRequest request = new ViviendaRequest(
                "Calle Corrida", "10", null,
                "Gijón", "123", "Asturias");

        mockMvc.perform(post("/api/viviendas")
                .with(user("test"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertirAJson(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeDevolver409CuandoLaViviendaEstaDuplicada() throws Exception {
        when(viviendaService.registrar(any(Vivienda.class)))
                .thenThrow(new ViviendaDuplicadaException(
                        "La vivienda con esa dirección ya existe"));

        ViviendaRequest request = new ViviendaRequest(
                "Calle Corrida", "10", null,
                "Gijón", "33206", "Asturias");

        mockMvc.perform(post("/api/viviendas")
                .with(user("test"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertirAJson(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Vivienda duplicada"));
    }

    private Vivienda crearVivienda() {
        Vivienda vivienda = new Vivienda();
        ReflectionTestUtils.setField(vivienda, "id", 1L);
        vivienda.setDireccion("Calle Corrida");
        vivienda.setNumero("10");
        vivienda.setLocalidad("Gijón");
        vivienda.setCodigoPostal("33206");
        vivienda.setProvincia("Asturias");
        vivienda.setDireccionNormalizada(
                "calle corrida, 10, gijon, 33206, asturias");
        vivienda.setFechaRegistro(LocalDateTime.now());
        return vivienda;
    }
}