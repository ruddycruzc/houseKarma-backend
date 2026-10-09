
package com.housekarma.housekarma.usuario.service;

import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void debeRegistrarUsuarioCodificandoLaContrasena() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Ana");
        usuario.setApellidos("Garcia");
        usuario.setEmail("ana.registro@example.com");
        usuario.setPassword("password-plano");
        usuario.setRol(Rol.INQUILINO);
        usuario.setFechaRegistro(LocalDateTime.now());

        when(usuarioRepository.existsByEmail(usuario.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode("password-plano"))
                .thenReturn("hash-codificado");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.registrarUsuario(usuario);

        assertEquals("hash-codificado", resultado.getPassword());

        verify(usuarioRepository).existsByEmail(usuario.getEmail());
        verify(passwordEncoder).encode("password-plano");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void debeRechazarRegistroCuandoElEmailYaExiste() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Ana");
        usuario.setApellidos("Garcia");
        usuario.setEmail("ana.registro@example.com");
        usuario.setPassword("password-plano");
        usuario.setRol(Rol.INQUILINO);
        usuario.setFechaRegistro(LocalDateTime.now());

        when(usuarioRepository.existsByEmail(usuario.getEmail()))
                .thenReturn(true);

        assertThrows(
                EmailYaRegistradoException.class,
                () -> usuarioService.registrarUsuario(usuario));

        verify(usuarioRepository).existsByEmail(usuario.getEmail());
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(passwordEncoder, never()).encode(any(String.class));
    }

}
