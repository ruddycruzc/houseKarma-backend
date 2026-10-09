
package com.housekarma.housekarma.usuario.repository;

import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
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
        Optional<Usuario> resultado =
                usuarioRepository.findById(usuarioGuardado.getId());

        // Assert
        assertThat(usuarioGuardado.getId()).isNotNull();
        assertThat(resultado).isPresent();
        assertThat(resultado.orElseThrow().getEmail())
                .isEqualTo("ana.garcia@example.com");
    }
}
