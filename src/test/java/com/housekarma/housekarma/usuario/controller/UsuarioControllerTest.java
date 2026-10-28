package com.housekarma.housekarma.usuario.controller;

import com.housekarma.housekarma.config.GlobalExceptionHandler;
import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.usuario.service.EmailYaRegistradoException;
import com.housekarma.housekarma.usuario.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

        @Mock
        private UsuarioService usuarioService;

        private MockMvc mockMvc;
        private LocalValidatorFactoryBean validator;

        @BeforeEach
        void setUp() {
                validator = new LocalValidatorFactoryBean();
                validator.afterPropertiesSet();

                mockMvc = standaloneSetup(new UsuarioController(usuarioService))
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .setValidator(validator)
                                .build();
        }

        @Test
        void debeRegistrarUsuarioYDevolverRespuestaSinContrasena()
                        throws Exception {

                Usuario usuario = new Usuario();
                usuario.setNombre("Ana");
                usuario.setApellidos("Garcia");
                usuario.setEmail("ana@example.com");
                usuario.setTelefono("600123456");
                usuario.setPassword("hash-codificado");
                usuario.setRol(Rol.INQUILINO);

                when(usuarioService.registrarUsuario(any()))
                                .thenReturn(usuario);

                mockMvc.perform(post("/api/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "nombre": "Ana",
                                                  "apellidos": "Garcia",
                                                  "email": "ana@example.com",
                                                  "confirmarEmail": "ana@example.com",
                                                  "password": "Password123",
                                                  "confirmarPassword": "Password123",
                                                  "telefono": "600123456",
                                                  "rol": "INQUILINO"
                                                }
                                                """))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.nombre").value("Ana"))
                                .andExpect(jsonPath("$.email").value("ana@example.com"))
                                .andExpect(jsonPath("$.password").doesNotExist())
                                .andExpect(jsonPath("$.confirmarPassword").doesNotExist());

                verify(usuarioService).registrarUsuario(any());
        }

        @Test
        void debeRechazarDatosInvalidosSinLlamarAlServicio()
                        throws Exception {

                mockMvc.perform(post("/api/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "nombre": "",
                                                  "apellidos": "Garcia",
                                                  "email": "correo-invalido",
                                                  "confirmarEmail": "otro@example.com",
                                                  "password": "Password123",
                                                  "confirmarPassword": "Password456",
                                                  "telefono": "600123456",
                                                  "rol": "INQUILINO"
                                                }
                                                """))
                                .andExpect(status().isBadRequest());

                org.mockito.Mockito.verifyNoInteractions(usuarioService);
        }

        @Test
        void debeDevolverConflictCuandoElEmailYaExiste()
                        throws Exception {

                when(usuarioService.registrarUsuario(any()))
                                .thenThrow(
                                                new EmailYaRegistradoException("ana@example.com"));

                mockMvc.perform(post("/api/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "nombre": "Ana",
                                                  "apellidos": "Garcia",
                                                  "email": "ana@example.com",
                                                  "confirmarEmail": "ana@example.com",
                                                  "password": "Password123",
                                                  "confirmarPassword": "Password123",
                                                  "telefono": "600123456",
                                                  "rol": "INQUILINO"
                                                }
                                                """))
                                .andExpect(status().isConflict());
        }

        @Test
        void debeDevolverErroresDeValidacionPorCampo()
                        throws Exception {

                mockMvc.perform(post("/api/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "nombre": "Ana",
                                                  "apellidos": "Garcia",
                                                  "email": "correo-invalido",
                                                  "confirmarEmail": "correo-invalido",
                                                  "password": "Password123",
                                                  "confirmarPassword": "Password123",
                                                  "telefono": "600123456",
                                                  "rol": "INQUILINO"
                                                }
                                                """))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.errors.email").exists());
        }

        @Test
        void debeRegistrarUsuarioConRolCasero() throws Exception {
                Usuario usuario = new Usuario();
                usuario.setNombre("Ana");
                usuario.setApellidos("Garcia");
                usuario.setEmail("ana@example.com");
                usuario.setTelefono("600123456");
                usuario.setRol(Rol.CASERO);

                when(usuarioService.registrarUsuario(any()))
                                .thenReturn(usuario);

                mockMvc.perform(post("/api/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "nombre": "Ana",
                                                  "apellidos": "Garcia",
                                                  "email": "ana@example.com",
                                                  "confirmarEmail": "ana@example.com",
                                                  "password": "Password123",
                                                  "confirmarPassword": "Password123",
                                                  "telefono": "600123456",
                                                  "rol": "CASERO"
                                                }
                                                """))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.rol").value("CASERO"));

                verify(usuarioService).registrarUsuario(any());
        }

        @Test
        void debeDevolverPerfilSinTelefonoNiContrasena() throws Exception {
                Usuario usuario = new Usuario();
                usuario.setNombre("Ana");
                usuario.setApellidos("Garcia");
                usuario.setEmail("ana@example.com");
                usuario.setAvatar("https://example.com/avatar.png");
                usuario.setTelefono("600123456");
                usuario.setPassword("hash-codificado");
                usuario.setRol(Rol.INQUILINO);

                when(usuarioService.buscarPorEmail("ana@example.com"))
                                .thenReturn(usuario);

                mockMvc.perform(get("/api/usuarios/buscar")
                                .param("email", "ana@example.com"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombre").value("Ana"))
                                .andExpect(jsonPath("$.apellidos").value("Garcia"))
                                .andExpect(jsonPath("$.avatar")
                                                .value("https://example.com/avatar.png"))
                                .andExpect(jsonPath("$.rol").value("INQUILINO"))
                                .andExpect(jsonPath("$.email").value("ana@example.com"))
                                .andExpect(jsonPath("$.telefono").doesNotExist())
                                .andExpect(jsonPath("$.password").doesNotExist());

                verify(usuarioService).buscarPorEmail("ana@example.com");
        }

        @Test
        void debeBuscarUsuariosPorNombreYDevolverDatosPublicos() throws Exception {
                Usuario usuario = new Usuario();
                usuario.setNombre("Carlos");
                usuario.setApellidos("Fernández López");
                usuario.setAvatar("https://example.com/avatar.png");
                usuario.setRol(Rol.CASERO);

                when(usuarioService.buscarUsuariosPorNombre(
                                "Fernández", "ana@example.com"))
                                .thenReturn(List.of(usuario));

                mockMvc.perform(get("/api/usuarios/buscar-usuarios")
                                .param("termino", "Fernández")
                                .principal(new UsernamePasswordAuthenticationToken(
                                                "ana@example.com", null)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].nombre").value("Carlos"))
                                .andExpect(jsonPath("$[0].apellidos")
                                                .value("Fernández López"))
                                .andExpect(jsonPath("$[0].avatar")
                                                .value("https://example.com/avatar.png"))
                                .andExpect(jsonPath("$[0].rol").value("CASERO"))
                                .andExpect(jsonPath("$[0].email").doesNotExist())
                                .andExpect(jsonPath("$[0].telefono").doesNotExist())
                                .andExpect(jsonPath("$[0].password").doesNotExist());

                verify(usuarioService).buscarUsuariosPorNombre(
                                "Fernández", "ana@example.com");
        }

        @Test
        void debeDevolverListaVaciaCuandoNoHayUsuariosCoincidentes()
                        throws Exception {

                when(usuarioService.buscarUsuariosPorNombre(
                                "NoExiste", "ana@example.com"))
                                .thenReturn(List.of());

                mockMvc.perform(get("/api/usuarios/buscar-usuarios")
                                .param("termino", "NoExiste")
                                .principal(new UsernamePasswordAuthenticationToken(
                                                "ana@example.com", null)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isEmpty());

                verify(usuarioService).buscarUsuariosPorNombre(
                                "NoExiste", "ana@example.com");
        }

        @Test
        void debeObtenerMiPerfilUsandoElEmailAutenticado() throws Exception {
                Usuario usuario = new Usuario();
                usuario.setNombre("Ana");
                usuario.setApellidos("García");
                usuario.setEmail("ana@example.com");
                usuario.setTelefono("600123456");
                usuario.setAvatar("https://example.com/avatar.png");
                usuario.setRol(Rol.INQUILINO);

                when(usuarioService.buscarPorEmail("ana@example.com"))
                                .thenReturn(usuario);

                mockMvc.perform(get("/api/usuarios/me")
                                .principal(new UsernamePasswordAuthenticationToken(
                                                "ana@example.com", null)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombre").value("Ana"))
                                .andExpect(jsonPath("$.apellidos").value("García"))
                                .andExpect(jsonPath("$.email").value("ana@example.com"))
                                .andExpect(jsonPath("$.telefono").value("600123456"))
                                .andExpect(jsonPath("$.avatar")
                                                .value("https://example.com/avatar.png"))
                                .andExpect(jsonPath("$.rol").value("INQUILINO"))
                                .andExpect(jsonPath("$.password").doesNotExist());

                verify(usuarioService).buscarPorEmail("ana@example.com");
        }

        @Test
        void debeActualizarMiPerfilUsandoElEmailAutenticado() throws Exception {
                Usuario usuario = new Usuario();
                usuario.setNombre("Ana Actualizada");
                usuario.setApellidos("García López");
                usuario.setEmail("ana@example.com");
                usuario.setTelefono("611223344");
                usuario.setAvatar("https://example.com/nuevo-avatar.png");
                usuario.setRol(Rol.INQUILINO);

                when(usuarioService.actualizarPerfil(
                                org.mockito.ArgumentMatchers.eq("ana@example.com"),
                                any()))
                                .thenReturn(usuario);

                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .patch("/api/usuarios/me")
                                .principal(new UsernamePasswordAuthenticationToken(
                                                "ana@example.com", null))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "nombre": "Ana Actualizada",
                                                  "apellidos": "García López",
                                                  "telefono": "611223344",
                                                  "avatar": "https://example.com/nuevo-avatar.png"
                                                }
                                                """))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nombre").value("Ana Actualizada"))
                                .andExpect(jsonPath("$.apellidos").value("García López"))
                                .andExpect(jsonPath("$.email").value("ana@example.com"))
                                .andExpect(jsonPath("$.telefono").value("611223344"))
                                .andExpect(jsonPath("$.avatar")
                                                .value("https://example.com/nuevo-avatar.png"))
                                .andExpect(jsonPath("$.rol").value("INQUILINO"));

                verify(usuarioService).actualizarPerfil(
                                org.mockito.ArgumentMatchers.eq("ana@example.com"),
                                any());
        }

        @Test
        void debeRechazarLaActualizacionSiElNombreEstaVacio() throws Exception {
                mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .patch("/api/usuarios/me")
                                .principal(new UsernamePasswordAuthenticationToken(
                                                "ana@example.com", null))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                  "nombre": "",
                                                  "apellidos": "García",
                                                  "telefono": "600123456",
                                                  "avatar": "https://example.com/avatar.png"
                                                }
                                                """))
                                .andExpect(status().isBadRequest());

                org.mockito.Mockito.verifyNoInteractions(usuarioService);
        }

}
