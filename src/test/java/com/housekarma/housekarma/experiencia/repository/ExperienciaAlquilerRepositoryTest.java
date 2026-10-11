package com.housekarma.housekarma.experiencia.repository;

import com.housekarma.housekarma.experiencia.entity.ExperienciaAlquiler;
import com.housekarma.housekarma.experiencia.entity.ExperienciaAlquiler.EstadoExperiencia;
import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.vivienda.entity.Vivienda;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class ExperienciaAlquilerRepositoryTest {

    @Autowired
    private ExperienciaAlquilerRepository experienciaRepository;

    @Autowired
    private org.springframework.data.jpa.repository.JpaRepository<
            Vivienda, Long> viviendaRepository;

    @Autowired
    private org.springframework.data.jpa.repository.JpaRepository<
            Usuario, Long> usuarioRepository;

    @Test
    void debeGuardarExperienciaYConsultarlaPorVivienda() {
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

        vivienda = viviendaRepository.save(vivienda);

        Usuario inquilino = crearUsuario(
                "inquilino@test.com", Rol.INQUILINO
        );
        Usuario casero = crearUsuario(
                "casero@test.com", Rol.CASERO
        );

        ExperienciaAlquiler experiencia = new ExperienciaAlquiler();
        experiencia.setVivienda(vivienda);
        experiencia.setInquilino(inquilino);
        experiencia.setCasero(casero);
        experiencia.setFechaInicio(LocalDate.of(2025, 1, 1));
        experiencia.setEstado(EstadoExperiencia.PENDIENTE_CONFIRMACION);
        experiencia.setFechaRegistro(LocalDateTime.now());

        ExperienciaAlquiler guardada =
                experienciaRepository.save(experiencia);

        assertThat(guardada.getId()).isNotNull();
        assertThat(
                experienciaRepository.findAllByViviendaId(vivienda.getId())
        ).hasSize(1);
    }

    private Usuario crearUsuario(String email, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setNombre("Usuario");
        usuario.setApellidos("Prueba");
        usuario.setEmail(email);
        usuario.setPassword("password-cifrada");
        usuario.setRol(rol);
        usuario.setFechaRegistro(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }
}