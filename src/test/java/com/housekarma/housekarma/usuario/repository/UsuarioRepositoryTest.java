
package com.housekarma.housekarma.usuario.repository;

import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void debeGuardarYRecuperarUsuarioPorId() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setNombre("Ana");
        usuario.setApellidos("García López");
        usuario.setEmail("ana.garcia@example.com");
        usuario.setPassword("hash-de-prueba");
        usuario.setRol(Rol.INQUILINO);
        usuario.setFechaRegistro(LocalDateTime.now());

        // Act
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        Optional<Usuario> resultado = usuarioRepository.findById(usuarioGuardado.getId());

        // Assert
        assertThat(usuarioGuardado.getId()).isNotNull();
        assertThat(resultado).isPresent();
        assertThat(resultado.orElseThrow().getEmail())
                .isEqualTo("ana.garcia@example.com");
    }

    @Test
    void debeEncontrarUsuarioPorEmail() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Ana");
        usuario.setApellidos("Garcia");
        usuario.setEmail("ana.busqueda@example.com");
        usuario.setPassword("hash-de-prueba");
        usuario.setRol(Rol.INQUILINO);
        usuario.setFechaRegistro(LocalDateTime.now());

        usuarioRepository.save(usuario);

        var resultado = usuarioRepository.findByEmail("ana.busqueda@example.com");

        assertTrue(resultado.isPresent());
        assertEquals("ana.busqueda@example.com", resultado.get().getEmail());
    }

    @Test
    void debeDevolverVacioCuandoElEmailNoExiste() {
        var resultado = usuarioRepository.findByEmail("inexistente@example.com");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void debeComprobarSiExisteUnEmailRegistrado() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Ana");
        usuario.setApellidos("Garcia");
        usuario.setEmail("ana.existe@example.com");
        usuario.setPassword("hash-de-prueba");
        usuario.setRol(Rol.INQUILINO);
        usuario.setFechaRegistro(LocalDateTime.now());

        usuarioRepository.save(usuario);

        assertTrue(usuarioRepository.existsByEmail("ana.existe@example.com"));
        assertFalse(usuarioRepository.existsByEmail("noexiste@example.com"));
    }

    @Test
    void debeBuscarUsuariosPorNombreOApellidosIgnorandoMayusculas() {
        Usuario casero = crearUsuario(
                "Ana",
                "García López",
                "ana.garcia.test@example.com",
                Rol.CASERO);

        usuarioRepository.save(casero);

        List<Usuario> resultados = usuarioRepository.buscarPorRolYNombreOApellidos(
                Rol.CASERO,
                "GARCÍA");

        assertThat(resultados)
                .extracting(usuario -> usuario.getEmail())
                .contains("ana.garcia.test@example.com");
    }

    @Test
    void debeExcluirUsuariosDeOtrosRolesEnLaBusqueda() {
        usuarioRepository.save(crearUsuario(
                "Carlos",
                "García López",
                "carlos.garcia.test@example.com",
                Rol.CASERO));

        usuarioRepository.save(crearUsuario(
                "Lucía",
                "García López",
                "lucia.garcia.test@example.com",
                Rol.INQUILINO));

        List<Usuario> resultados = usuarioRepository.buscarPorRolYNombreOApellidos(
                Rol.CASERO,
                "García");

        assertThat(resultados)
                .extracting(usuario -> usuario.getEmail())
                .containsExactly("carlos.garcia.test@example.com");
    }

    @Test
    void debeDevolverListaVaciaSiNoHayCoincidencias() {
        List<Usuario> resultados = usuarioRepository.buscarPorRolYNombreOApellidos(
                Rol.CASERO,
                "NombreInexistente");

        assertThat(resultados).isEmpty();
    }

    private Usuario crearUsuario(
            String nombre,
            String apellidos,
            String email,
            Rol rol) {
        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setEmail(email);
        usuario.setPassword("password-de-prueba");
        usuario.setRol(rol);
        usuario.setFechaRegistro(LocalDateTime.now());

        return usuario;
    }
}
