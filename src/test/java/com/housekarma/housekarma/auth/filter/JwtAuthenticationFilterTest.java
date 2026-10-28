package com.housekarma.housekarma.auth.filter;

import com.housekarma.housekarma.auth.service.JwtService;
import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private static final String SECRET_KEY =
            "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    private JwtService jwtService;
    private UsuarioRepository usuarioRepository;
    private JwtAuthenticationFilter filter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET_KEY, 3_600_000L);
        usuarioRepository = mock(UsuarioRepository.class);
        filter = new JwtAuthenticationFilter(jwtService, usuarioRepository);
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    void debeContinuarSinAutenticarCuandoNoHayToken() throws Exception {
        ejecutarFiltro(null);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(any(), any());
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void debeContinuarSinAutenticarCuandoLaCabeceraNoEsBearer()
            throws Exception {
        ejecutarFiltro("Basic credenciales");

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(any(), any());
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void debeRechazarUnTokenManipulado() throws Exception {
        ejecutarFiltro("Bearer token.invalido.firma");

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(any(), any());
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void debeAutenticarAlUsuarioCuandoElTokenEsValido() throws Exception {
        Usuario usuario = crearUsuario(
                "ana@example.com", Rol.INQUILINO);

        when(usuarioRepository.findByEmail("ana@example.com"))
                .thenReturn(Optional.of(usuario));

        String token = jwtService.generarToken(usuario);
        ejecutarFiltro("Bearer " + token);

        var authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals("ana@example.com", authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_INQUILINO")));

        verify(usuarioRepository).findByEmail("ana@example.com");
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void debeContinuarSinAutenticarSiElUsuarioNoExiste()
            throws Exception {
        Usuario usuario = crearUsuario(
                "desconocido@example.com", Rol.CASERO);

        when(usuarioRepository.findByEmail("desconocido@example.com"))
                .thenReturn(Optional.empty());

        String token = jwtService.generarToken(usuario);
        ejecutarFiltro("Bearer " + token);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(usuarioRepository).findByEmail("desconocido@example.com");
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void noDebeSobrescribirUnaAutenticacionExistente() throws Exception {
        var autenticacionExistente =
                new UsernamePasswordAuthenticationToken(
                        "usuario-actual", null, java.util.List.of());

        SecurityContextHolder.getContext()
                .setAuthentication(autenticacionExistente);

        Usuario usuarioToken = crearUsuario(
                "ana@example.com", Rol.INQUILINO);

        String token = jwtService.generarToken(usuarioToken);
        ejecutarFiltro("Bearer " + token);

        assertSame(
                autenticacionExistente,
                SecurityContextHolder.getContext().getAuthentication());

        verifyNoInteractions(usuarioRepository);
        verify(filterChain).doFilter(any(), any());
    }

    private void ejecutarFiltro(String authorization)
            throws IOException, jakarta.servlet.ServletException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        if (authorization != null) {
            request.addHeader("Authorization", authorization);
        }

        filter.doFilter(request, response, filterChain);
    }

    private Usuario crearUsuario(String email, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setNombre("Usuario");
        usuario.setApellidos("Prueba");
        usuario.setEmail(email);
        usuario.setPassword("hash-codificado");
        usuario.setRol(rol);
        return usuario;
    }
}