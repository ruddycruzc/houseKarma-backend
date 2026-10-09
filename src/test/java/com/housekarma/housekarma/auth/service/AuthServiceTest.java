package com.housekarma.housekarma.auth.service;

import com.housekarma.housekarma.auth.dto.AuthResponse;
import com.housekarma.housekarma.auth.dto.LoginRequest;
import com.housekarma.housekarma.auth.exception.CredencialesInvalidasException;
import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                usuarioRepository,
                passwordEncoder,
                jwtService
        );
    }

    @Test
    void debeAutenticarUsuarioConCredencialesCorrectas() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Ana");
        usuario.setApellidos("García");
        usuario.setEmail("ana@example.com");
        usuario.setPassword("$2a$12$hashDeEjemplo");
        usuario.setRol(Rol.INQUILINO);

        when(usuarioRepository.findByEmail("ana@example.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(
                "ClaveSegura123",
                "$2a$12$hashDeEjemplo"
        )).thenReturn(true);
        when(jwtService.generarToken(usuario))
                .thenReturn("token-de-prueba");

        AuthResponse response = authService.login(
                new LoginRequest("ana@example.com", "ClaveSegura123")
        );

        assertEquals("token-de-prueba", response.token());
        assertEquals("ana@example.com", response.email());
        assertEquals("INQUILINO", response.rol());
        verify(jwtService).generarToken(usuario);
    }

    @Test
    void debeRechazarEmailInexistente() {
        when(usuarioRepository.findByEmail("desconocido@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                CredencialesInvalidasException.class,
                () -> authService.login(
                        new LoginRequest(
                                "desconocido@example.com",
                                "ClaveSegura123"
                        )
                )
        );

        verify(jwtService, never()).generarToken(any());
    }

    @Test
    void debeRechazarContrasenaIncorrecta() {
        Usuario usuario = new Usuario();
        usuario.setEmail("ana@example.com");
        usuario.setPassword("$2a$12$hashDeEjemplo");

        when(usuarioRepository.findByEmail("ana@example.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(
                "Incorrecta123",
                "$2a$12$hashDeEjemplo"
        )).thenReturn(false);

        assertThrows(
                CredencialesInvalidasException.class,
                () -> authService.login(
                        new LoginRequest(
                                "ana@example.com",
                                "Incorrecta123"
                        )
                )
        );

        verify(jwtService, never()).generarToken(any());
    }
}