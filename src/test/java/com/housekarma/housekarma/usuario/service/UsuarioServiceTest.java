package com.housekarma.housekarma.usuario.service;

import com.housekarma.housekarma.usuario.dto.UsuarioRegistroRequest;
import com.housekarma.housekarma.usuario.dto.UsuarioPerfilActualizarRequest;
import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

        @Mock
        private UsuarioRepository usuarioRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @InjectMocks
        private UsuarioService usuarioService;

        @Test
        void debeRegistrarUsuarioConRolInquilino() {
                UsuarioRegistroRequest request = crearRequest("INQUILINO");
                Usuario resultado = registrarUsuarioCorrectamente(request);

                assertEquals("Ana", resultado.getNombre());
                assertEquals("Garcia", resultado.getApellidos());
                assertEquals(request.email(), resultado.getEmail());
                assertEquals(request.telefono(), resultado.getTelefono());
                assertEquals("hash-codificado", resultado.getPassword());
                assertEquals(Rol.INQUILINO, resultado.getRol());
                assertNotNull(resultado.getFechaRegistro());

                verificarRegistroCorrecto(request);
        }

        @Test
        void debeRegistrarUsuarioConRolCasero() {
                UsuarioRegistroRequest request = crearRequest("CASERO");
                Usuario resultado = registrarUsuarioCorrectamente(request);

                assertEquals(Rol.CASERO, resultado.getRol());

                verificarRegistroCorrecto(request);
        }

        @Test
        void debeRechazarRegistroConRolAdmin() {
                UsuarioRegistroRequest request = crearRequest("ADMIN");

                assertThrows(
                                IllegalArgumentException.class,
                                () -> usuarioService.registrarUsuario(request));

                verificarRolRechazado(request);
        }

        @Test
        void debeRechazarRegistroConRolDesconocido() {
                UsuarioRegistroRequest request = crearRequest("DESCONOCIDO");

                assertThrows(
                                IllegalArgumentException.class,
                                () -> usuarioService.registrarUsuario(request));

                verificarRolRechazado(request);
        }

        @Test
        void debeRechazarRegistroCuandoElEmailYaExiste() {
                UsuarioRegistroRequest request = crearRequest("INQUILINO");

                when(usuarioRepository.existsByEmail(request.email()))
                                .thenReturn(true);

                assertThrows(
                                EmailYaRegistradoException.class,
                                () -> usuarioService.registrarUsuario(request));

                verify(usuarioRepository).existsByEmail(request.email());
                verify(usuarioRepository, never()).save(any(Usuario.class));
                verify(passwordEncoder, never()).encode(any(String.class));
        }

        @Test
        void debeEncontrarUsuarioPorEmail() {
                String email = "ana@example.com";

                Usuario usuario = new Usuario();
                usuario.setNombre("Ana");
                usuario.setApellidos("García");
                usuario.setEmail(email);
                usuario.setRol(Rol.INQUILINO);

                when(usuarioRepository.findByEmail(email))
                                .thenReturn(Optional.of(usuario));

                Usuario resultado = usuarioService.buscarPorEmail(email);

                assertSame(usuario, resultado);
                verify(usuarioRepository).findByEmail(email);
        }

        @Test
        void debeActualizarPerfilSinModificarDatosSensibles() {
                String email = "ana@example.com";

                Usuario usuario = new Usuario();
                usuario.setNombre("Ana");
                usuario.setApellidos("García");
                usuario.setEmail(email);
                usuario.setTelefono("600000000");
                usuario.setAvatar("avatar-antiguo");
                usuario.setPassword("hash-original");
                usuario.setRol(Rol.INQUILINO);

                UsuarioPerfilActualizarRequest request = new UsuarioPerfilActualizarRequest(
                                "Ana María",
                                "García López",
                                "611222333",
                                "https://example.com/avatar.png");

                when(usuarioRepository.findByEmail(email))
                                .thenReturn(Optional.of(usuario));

                when(usuarioRepository.save(any(Usuario.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                Usuario resultado = usuarioService.actualizarPerfil(email, request);

                assertEquals("Ana María", resultado.getNombre());
                assertEquals("García López", resultado.getApellidos());
                assertEquals("611222333", resultado.getTelefono());
                assertEquals(
                                "https://example.com/avatar.png",
                                resultado.getAvatar());

                assertEquals(email, resultado.getEmail());
                assertEquals("hash-original", resultado.getPassword());
                assertEquals(Rol.INQUILINO, resultado.getRol());

                verify(usuarioRepository).findByEmail(email);
                verify(usuarioRepository).save(usuario);
        }

        @Test
        void debeLanzarExcepcionCuandoNoEncuentraUsuarioPorEmail() {
                String email = "inexistente@example.com";

                when(usuarioRepository.findByEmail(email))
                                .thenReturn(Optional.empty());

                assertThrows(
                                UsuarioNoEncontradoException.class,
                                () -> usuarioService.buscarPorEmail(email));

                verify(usuarioRepository).findByEmail(email);
        }

        private UsuarioRegistroRequest crearRequest(String rol) {
                return new UsuarioRegistroRequest(
                                "Ana",
                                "Garcia",
                                "ana.registro@example.com",
                                "ana.registro@example.com",
                                "Password123",
                                "Password123",
                                "600123456",
                                rol);
        }

        private Usuario registrarUsuarioCorrectamente(
                        UsuarioRegistroRequest request) {

                when(usuarioRepository.existsByEmail(request.email()))
                                .thenReturn(false);

                when(passwordEncoder.encode(request.password()))
                                .thenReturn("hash-codificado");

                when(usuarioRepository.save(any(Usuario.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                return usuarioService.registrarUsuario(request);
        }

        private void verificarRegistroCorrecto(
                        UsuarioRegistroRequest request) {

                verify(usuarioRepository).existsByEmail(request.email());
                verify(passwordEncoder).encode(request.password());
                verify(usuarioRepository).save(any(Usuario.class));
        }

        private void verificarRolRechazado(
                        UsuarioRegistroRequest request) {

                verify(usuarioRepository, never()).existsByEmail(request.email());
                verify(passwordEncoder, never()).encode(any(String.class));
                verify(usuarioRepository, never()).save(any(Usuario.class));
        }
}