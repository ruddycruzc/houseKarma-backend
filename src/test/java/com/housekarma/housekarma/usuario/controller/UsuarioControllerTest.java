
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

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

}
