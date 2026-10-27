package com.housekarma.housekarma.usuario.controller;

import com.housekarma.housekarma.auth.service.JwtService;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;
import com.housekarma.housekarma.usuario.dto.UsuarioPerfilActualizarRequest;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import com.housekarma.housekarma.vivienda.service.ViviendaService;

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

  @MockitoBean
  private ViviendaService viviendaService;

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
              "telefono": "600123456",
              "rol": "INQUILINO"
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

  @Test
  void debeDenegarRegistroDeViviendaAInquilino() throws Exception {
    Usuario usuario = new Usuario();
    usuario.setEmail("ana@example.com");
    usuario.setRol(Rol.INQUILINO);

    when(jwtService.esTokenValido("token-inquilino"))
        .thenReturn(true);
    when(jwtService.extraerEmail("token-inquilino"))
        .thenReturn("ana@example.com");
    when(usuarioRepository.findByEmail("ana@example.com"))
        .thenReturn(Optional.of(usuario));

    mockMvc.perform(post("/api/viviendas")
        .header("Authorization", "Bearer token-inquilino")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "direccion": "Calle Mayor",
              "numero": "10",
              "complemento": "",
              "localidad": "Gijón",
              "codigoPostal": "33201",
              "provincia": "Asturias"
            }
            """))
        .andExpect(status().isForbidden());

    verify(viviendaService, never())
        .registrar(any(com.housekarma.housekarma.vivienda.entity.Vivienda.class));
  }

  @Test
  void debePermitirRegistroDeViviendaACasero() throws Exception {
    Usuario usuario = new Usuario();
    usuario.setEmail("casero@example.com");
    usuario.setRol(Rol.CASERO);

    when(jwtService.esTokenValido("token-casero"))
        .thenReturn(true);
    when(jwtService.extraerEmail("token-casero"))
        .thenReturn("casero@example.com");
    when(usuarioRepository.findByEmail("casero@example.com"))
        .thenReturn(Optional.of(usuario));

    when(viviendaService.registrar(any(
        com.housekarma.housekarma.vivienda.entity.Vivienda.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc.perform(post("/api/viviendas")
        .header("Authorization", "Bearer token-casero")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "direccion": "Calle Mayor",
              "numero": "10",
              "complemento": "",
              "localidad": "Gijón",
              "codigoPostal": "33201",
              "provincia": "Asturias"
            }
            """))
        .andExpect(status().isCreated());

    verify(viviendaService).registrar(any(
        com.housekarma.housekarma.vivienda.entity.Vivienda.class));
  }

  @Test
  void debeObtenerElPerfilDelUsuarioAutenticado() throws Exception {
    Usuario usuario = new Usuario();
    usuario.setNombre("Ana");
    usuario.setApellidos("García");
    usuario.setEmail("ana@example.com");
    usuario.setTelefono("600123456");
    usuario.setRol(Rol.INQUILINO);

    when(jwtService.esTokenValido("token-perfil")).thenReturn(true);
    when(jwtService.extraerEmail("token-perfil"))
        .thenReturn("ana@example.com");
    when(usuarioRepository.findByEmail("ana@example.com"))
        .thenReturn(Optional.of(usuario));
    when(usuarioService.buscarPorEmail("ana@example.com"))
        .thenReturn(usuario);

    mockMvc.perform(get("/api/usuarios/me")
        .header("Authorization", "Bearer token-perfil"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nombre").value("Ana"))
        .andExpect(jsonPath("$.apellidos").value("García"))
        .andExpect(jsonPath("$.email").value("ana@example.com"))
        .andExpect(jsonPath("$.telefono").value("600123456"))
        .andExpect(jsonPath("$.rol").value("INQUILINO"))
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  @Test
  void debeActualizarElPerfilDelUsuarioAutenticado() throws Exception {
    Usuario usuario = new Usuario();
    usuario.setNombre("Ana");
    usuario.setApellidos("García");
    usuario.setEmail("ana@example.com");
    usuario.setTelefono("600123456");
    usuario.setAvatar("https://example.com/avatar.png");
    usuario.setRol(Rol.INQUILINO);

    when(jwtService.esTokenValido("token-perfil")).thenReturn(true);
    when(jwtService.extraerEmail("token-perfil"))
        .thenReturn("ana@example.com");
    when(usuarioRepository.findByEmail("ana@example.com"))
        .thenReturn(Optional.of(usuario));
    when(usuarioService.actualizarPerfil(
        eq("ana@example.com"),
        any(UsuarioPerfilActualizarRequest.class)))
        .thenReturn(usuario);

    mockMvc.perform(patch("/api/usuarios/me")
        .header("Authorization", "Bearer token-perfil")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
                "nombre": "Ana",
                "apellidos": "García",
                "telefono": "600123456",
                "avatar": "https://example.com/avatar.png"
            }
            """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nombre").value("Ana"))
        .andExpect(jsonPath("$.apellidos").value("García"))
        .andExpect(jsonPath("$.telefono").value("600123456"))
        .andExpect(jsonPath("$.avatar")
            .value("https://example.com/avatar.png"))
        .andExpect(jsonPath("$.rol").value("INQUILINO"))
        .andExpect(jsonPath("$.password").doesNotExist());
  }

}
