package com.housekarma.housekarma.vivienda.repository;

import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;
import com.housekarma.housekarma.vivienda.entity.Vivienda;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ViviendaRepositoryTest {

    @Autowired
    private ViviendaRepository viviendaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void debeGuardarViviendaYEncontrarlaPorDireccionNormalizada() {
        Usuario casero = crearCasero();

        Vivienda vivienda = crearVivienda(casero);

        Vivienda guardada = viviendaRepository.saveAndFlush(vivienda);

        assertThat(guardada.getId()).isNotNull();

        assertThat(
                viviendaRepository.existsByDireccionNormalizada(
                        "calle corrida 10, gijon, 33206"
                )
        ).isTrue();

        Vivienda recuperada = viviendaRepository.findById(
                guardada.getId()
        ).orElseThrow();

        assertThat(recuperada.getCasero().getId())
                .isEqualTo(casero.getId());
    }

    private Usuario crearCasero() {
        Usuario casero = new Usuario();
        casero.setNombre("Carlos");
        casero.setApellidos("García");
        casero.setEmail("casero.repository@example.com");
        casero.setRol(Rol.CASERO);
        casero.setFechaRegistro(LocalDateTime.now());
        casero.setPassword("password-de-prueba");

        return usuarioRepository.saveAndFlush(casero);
    }

    private Vivienda crearVivienda(Usuario casero) {
        Vivienda vivienda = new Vivienda();
        vivienda.setCasero(casero);
        vivienda.setDireccion("Calle Corrida");
        vivienda.setNumero("10");
        vivienda.setLocalidad("Gijón");
        vivienda.setCodigoPostal("33206");
        vivienda.setProvincia("Asturias");
        vivienda.setDireccionNormalizada(
                "calle corrida 10, gijon, 33206"
        );
        vivienda.setFechaRegistro(LocalDateTime.now());

        return vivienda;
    }
}