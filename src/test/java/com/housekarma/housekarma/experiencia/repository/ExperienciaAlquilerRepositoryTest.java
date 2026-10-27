package com.housekarma.housekarma.experiencia.repository;

import com.housekarma.housekarma.experiencia.entity.ExperienciaAlquiler;
import com.housekarma.housekarma.experiencia.entity.ExperienciaAlquiler.EstadoExperiencia;
import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;
import com.housekarma.housekarma.vivienda.entity.Vivienda;
import com.housekarma.housekarma.vivienda.repository.ViviendaRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ExperienciaAlquilerRepositoryTest {

    @Autowired
    private ExperienciaAlquilerRepository experienciaRepository;

    @Autowired
    private ViviendaRepository viviendaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void debeGuardarExperienciaYConsultarlaPorVivienda() {
        Usuario casero = crearUsuario("casero@test.com", Rol.CASERO);
        Usuario inquilino = crearUsuario("inquilino@test.com", Rol.INQUILINO);

        Vivienda vivienda = crearVivienda(
                "Calle Corrida", "10", casero
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

    @Test
    void debeBuscarExperienciasPendientesSinConfirmacionDelCasero() {
        Usuario casero = crearUsuario("casero@test.com", Rol.CASERO);
        Usuario otroCasero = crearUsuario(
                "otro.casero@test.com", Rol.CASERO
        );
        Usuario inquilino = crearUsuario(
                "inquilino@test.com", Rol.INQUILINO
        );

        Vivienda vivienda = crearVivienda(
                "Calle Corrida", "10", casero
        );
        Vivienda otraVivienda = crearVivienda(
                "Calle Uría", "20", otroCasero
        );

        ExperienciaAlquiler pendiente = crearExperiencia(
                vivienda,
                inquilino,
                casero,
                EstadoExperiencia.PENDIENTE_CONFIRMACION,
                false
        );

        // No debe incluir experiencias ya confirmadas por el casero.
        crearExperiencia(
                vivienda,
                inquilino,
                casero,
                EstadoExperiencia.PENDIENTE_CONFIRMACION,
                true
        );

        // No debe incluir experiencias con otro estado.
        crearExperiencia(
                vivienda,
                inquilino,
                casero,
                EstadoExperiencia.CONFIRMADA,
                false
        );

        // No debe incluir experiencias de otro casero.
        crearExperiencia(
                otraVivienda,
                inquilino,
                otroCasero,
                EstadoExperiencia.PENDIENTE_CONFIRMACION,
                false
        );

        var resultado = experienciaRepository
                .findAllByCaseroIdAndEstadoAndConfirmacionCaseroFalse(
                        casero.getId(),
                        EstadoExperiencia.PENDIENTE_CONFIRMACION
                );

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId())
                .isEqualTo(pendiente.getId());
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

    private Vivienda crearVivienda(
            String direccion,
            String numero,
            Usuario casero
    ) {
        Vivienda vivienda = new Vivienda();
        vivienda.setCasero(casero);
        vivienda.setDireccion(direccion);
        vivienda.setNumero(numero);
        vivienda.setLocalidad("Gijón");
        vivienda.setCodigoPostal("33206");
        vivienda.setProvincia("Asturias");
        vivienda.setDireccionNormalizada(
                direccion.toLowerCase() + " " + numero
        );
        vivienda.setFechaRegistro(LocalDateTime.now());

        return viviendaRepository.save(vivienda);
    }

    private ExperienciaAlquiler crearExperiencia(
            Vivienda vivienda,
            Usuario inquilino,
            Usuario casero,
            EstadoExperiencia estado,
            boolean confirmacionCasero
    ) {
        ExperienciaAlquiler experiencia = new ExperienciaAlquiler();
        experiencia.setVivienda(vivienda);
        experiencia.setInquilino(inquilino);
        experiencia.setCasero(casero);
        experiencia.setFechaInicio(LocalDate.of(2025, 1, 1));
        experiencia.setEstado(estado);
        experiencia.setConfirmacionInquilino(true);
        experiencia.setConfirmacionCasero(confirmacionCasero);
        experiencia.setFechaRegistro(LocalDateTime.now());

        return experienciaRepository.save(experiencia);
    }
}