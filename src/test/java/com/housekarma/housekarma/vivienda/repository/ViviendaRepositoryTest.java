package com.housekarma.housekarma.vivienda.repository;

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

    @Test
    void debeGuardarViviendaYEncontrarlaPorDireccionNormalizada() {
        Vivienda vivienda = new Vivienda();
        vivienda.setDireccion("Calle Corrida");
        vivienda.setNumero("10");
        vivienda.setLocalidad("Gijón");
        vivienda.setCodigoPostal("33206");
        vivienda.setProvincia("Asturias");
        vivienda.setDireccionNormalizada(
                "calle corrida 10, gijon, 33206"
        );
        vivienda.setFechaRegistro(LocalDateTime.now());

        Vivienda guardada = viviendaRepository.save(vivienda);

        assertThat(guardada.getId()).isNotNull();
        assertThat(
                viviendaRepository.existsByDireccionNormalizada(
                        "calle corrida 10, gijon, 33206"
                )
        ).isTrue();
    }
}