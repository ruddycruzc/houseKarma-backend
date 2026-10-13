package com.housekarma.housekarma.experiencia.service;

import com.housekarma.housekarma.experiencia.entity.ExperienciaAlquiler;
import com.housekarma.housekarma.experiencia.repository.ExperienciaAlquilerRepository;
import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;
import com.housekarma.housekarma.vivienda.entity.Vivienda;
import com.housekarma.housekarma.vivienda.repository.ViviendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExperienciaAlquilerServiceTest {

    @Mock
    private ExperienciaAlquilerRepository experienciaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ViviendaRepository viviendaRepository;

    @InjectMocks
    private ExperienciaAlquilerService experienciaService;

    private Usuario inquilino;
    private Usuario casero;
    private Vivienda vivienda;

    @BeforeEach
    void setUp() {
        inquilino = crearUsuario(1L, "inquilino@test.com", Rol.INQUILINO);
        casero = crearUsuario(2L, "casero@test.com", Rol.CASERO);

        vivienda = new Vivienda();
        vivienda.setDireccion("Calle Corrida");
        vivienda.setNumero("10");
        vivienda.setLocalidad("Gijón");
        vivienda.setCodigoPostal("33206");
        vivienda.setProvincia("Asturias");
        vivienda.setDireccionNormalizada("calle corrida 10, gijon, 33206");
        vivienda.setCasero(casero);
        vivienda.setFechaRegistro(LocalDateTime.now());
    }

    @Test
    void debeRegistrarExperienciaCuandoIniciaElInquilino() {
        when(usuarioRepository.findByEmail(inquilino.getEmail()))
                .thenReturn(Optional.of(inquilino));
        when(viviendaRepository.findById(10L))
                .thenReturn(Optional.of(vivienda));
        when(experienciaRepository.save(any(ExperienciaAlquiler.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ExperienciaAlquiler resultado = experienciaService.registrarExperiencia(
                inquilino.getEmail(),
                10L,
                null,
                LocalDate.of(2025, 1, 1),
                null);

        assertEquals(inquilino, resultado.getInquilino());
        assertEquals(casero, resultado.getCasero());
        assertEquals(vivienda, resultado.getVivienda());
        assertEquals(
                ExperienciaAlquiler.EstadoExperiencia.PENDIENTE_CONFIRMACION,
                resultado.getEstado());
        assertTrue(resultado.isConfirmacionInquilino());
        assertFalse(resultado.isConfirmacionCasero());
        assertNotNull(resultado.getFechaRegistro());

        verify(experienciaRepository).save(any(ExperienciaAlquiler.class));
    }

    @Test
    void debeRegistrarExperienciaCuandoIniciaElCasero() {
        when(usuarioRepository.findByEmail(casero.getEmail()))
                .thenReturn(Optional.of(casero));
        when(usuarioRepository.findById(inquilino.getId()))
                .thenReturn(Optional.of(inquilino));
        when(viviendaRepository.findById(10L))
                .thenReturn(Optional.of(vivienda));
        when(experienciaRepository.save(any(ExperienciaAlquiler.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ExperienciaAlquiler resultado = experienciaService.registrarExperiencia(
                casero.getEmail(),
                10L,
                inquilino.getId(),
                LocalDate.of(2025, 1, 1),
                null);

        assertEquals(inquilino, resultado.getInquilino());
        assertEquals(casero, resultado.getCasero());
        assertTrue(resultado.isConfirmacionCasero());
        assertFalse(resultado.isConfirmacionInquilino());
        assertEquals(
                ExperienciaAlquiler.EstadoExperiencia.PENDIENTE_CONFIRMACION,
                resultado.getEstado());
    }

    @Test
    void debeRechazarLaExperienciaSiElInquilinoYElCaseroSonLaMismaPersona() {
        when(usuarioRepository.findByEmail(casero.getEmail()))
                .thenReturn(Optional.of(casero));
        when(viviendaRepository.findById(10L))
                .thenReturn(Optional.of(vivienda));
        when(usuarioRepository.findById(casero.getId()))
                .thenReturn(Optional.of(casero));

        assertThrows(
                IllegalArgumentException.class,
                () -> experienciaService.registrarExperiencia(
                        casero.getEmail(),
                        10L,
                        casero.getId(),
                        LocalDate.of(2025, 1, 1),
                        null));

        verify(experienciaRepository, never())
                .save(any(ExperienciaAlquiler.class));
    }

    @Test
    void debeRechazarUnaFechaFinAnteriorALaFechaInicio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> experienciaService.registrarExperiencia(
                        inquilino.getEmail(),
                        10L,
                        null,
                        LocalDate.of(2025, 5, 1),
                        LocalDate.of(2025, 4, 1)));

        verify(experienciaRepository, never())
                .save(any(ExperienciaAlquiler.class));
    }

    private Usuario crearUsuario(Long id, String email, Rol rol) {
        Usuario usuario = new Usuario() {
            @Override
            public Long getId() {
                return id;
            }
        };

        usuario.setNombre("Usuario");
        usuario.setApellidos("Prueba");
        usuario.setEmail(email);
        usuario.setPassword("password-cifrada");
        usuario.setRol(rol);
        usuario.setFechaRegistro(LocalDateTime.now());

        return usuario;
    }

    @Test
    void debeConfirmarLaExperienciaCuandoConfirmaElCasero() {
        ExperienciaAlquiler experiencia = crearExperienciaPendiente();

        when(experienciaRepository.findById(1L))
                .thenReturn(Optional.of(experiencia));
        when(usuarioRepository.findByEmail(casero.getEmail()))
                .thenReturn(Optional.of(casero));
        when(experienciaRepository.save(any(ExperienciaAlquiler.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ExperienciaAlquiler resultado = experienciaService.confirmarExperiencia(
                casero.getEmail(), 1L);

        assertTrue(resultado.isConfirmacionCasero());
        assertFalse(resultado.isConfirmacionInquilino());
        assertEquals(
                ExperienciaAlquiler.EstadoExperiencia.PENDIENTE_CONFIRMACION,
                resultado.getEstado());
    }

    @Test
    void debeCambiarAConfirmadaCuandoAmbasPartesConfirman() {
        ExperienciaAlquiler experiencia = crearExperienciaPendiente();
        experiencia.setConfirmacionInquilino(true);

        when(experienciaRepository.findById(1L))
                .thenReturn(Optional.of(experiencia));
        when(usuarioRepository.findByEmail(casero.getEmail()))
                .thenReturn(Optional.of(casero));
        when(experienciaRepository.save(any(ExperienciaAlquiler.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ExperienciaAlquiler resultado = experienciaService.confirmarExperiencia(
                casero.getEmail(), 1L);

        assertTrue(resultado.isConfirmacionInquilino());
        assertTrue(resultado.isConfirmacionCasero());
        assertEquals(
                ExperienciaAlquiler.EstadoExperiencia.CONFIRMADA,
                resultado.getEstado());
    }

    @Test
    void debeRechazarLaConfirmacionDeUnUsuarioAjeno() {
        Usuario ajeno = crearUsuario(
                3L, "ajeno@test.com", Rol.INQUILINO);
        ExperienciaAlquiler experiencia = crearExperienciaPendiente();

        when(experienciaRepository.findById(1L))
                .thenReturn(Optional.of(experiencia));
        when(usuarioRepository.findByEmail(ajeno.getEmail()))
                .thenReturn(Optional.of(ajeno));

        assertThrows(
                IllegalArgumentException.class,
                () -> experienciaService.confirmarExperiencia(
                        ajeno.getEmail(), 1L));

        verify(experienciaRepository, never())
                .save(any(ExperienciaAlquiler.class));
    }

    private ExperienciaAlquiler crearExperienciaPendiente() {
        ExperienciaAlquiler experiencia = new ExperienciaAlquiler();

        experiencia.setVivienda(vivienda);
        experiencia.setInquilino(inquilino);
        experiencia.setCasero(casero);
        experiencia.setFechaInicio(LocalDate.of(2025, 1, 1));
        experiencia.setEstado(
                ExperienciaAlquiler.EstadoExperiencia.PENDIENTE_CONFIRMACION);
        experiencia.setConfirmacionInquilino(false);
        experiencia.setConfirmacionCasero(false);
        experiencia.setFechaRegistro(LocalDateTime.now());

        return experiencia;
    }

    @Test
    void debeRechazarUnaConfirmacionDuplicada() {
        ExperienciaAlquiler experiencia = crearExperienciaPendiente();
        experiencia.setConfirmacionCasero(true);

        when(usuarioRepository.findByEmail(casero.getEmail()))
                .thenReturn(Optional.of(casero));
        when(experienciaRepository.findById(1L))
                .thenReturn(Optional.of(experiencia));

        assertThrows(
                IllegalArgumentException.class,
                () -> experienciaService.confirmarExperiencia(
                        casero.getEmail(), 1L));

        verify(experienciaRepository, never())
                .save(any(ExperienciaAlquiler.class));
    }

    @Test
    void debeRechazarLaConfirmacionDeUnaExperienciaCancelada() {
        verificarEstadoNoConfirmable(
                ExperienciaAlquiler.EstadoExperiencia.CANCELADA);
    }

    @Test
    void debeRechazarLaConfirmacionDeUnaExperienciaFinalizada() {
        verificarEstadoNoConfirmable(
                ExperienciaAlquiler.EstadoExperiencia.FINALIZADA);
    }

    @Test
    void debeRechazarUnCaseroQueNoEsPropietarioDeLaVivienda() {
        Usuario otroCasero = crearUsuario(
                3L, "otro.casero@test.com", Rol.CASERO);

        when(usuarioRepository.findByEmail(otroCasero.getEmail()))
                .thenReturn(Optional.of(otroCasero));
        when(viviendaRepository.findById(10L))
                .thenReturn(Optional.of(vivienda));

        assertThrows(
                IllegalArgumentException.class,
                () -> experienciaService.registrarExperiencia(
                        otroCasero.getEmail(),
                        10L,
                        inquilino.getId(),
                        LocalDate.of(2025, 1, 1),
                        null));

        verify(experienciaRepository, never())
                .save(any(ExperienciaAlquiler.class));
    }

    @Test
    void debeRechazarUnUsuarioConRolAdminAlIniciarExperiencia() {
        Usuario admin = crearUsuario(
                3L, "admin@test.com", Rol.ADMIN);

        when(usuarioRepository.findByEmail(admin.getEmail()))
                .thenReturn(Optional.of(admin));
        when(viviendaRepository.findById(10L))
                .thenReturn(Optional.of(vivienda));

        assertThrows(
                IllegalArgumentException.class,
                () -> experienciaService.registrarExperiencia(
                        admin.getEmail(),
                        10L,
                        null,
                        LocalDate.of(2025, 1, 1),
                        null));

        verify(experienciaRepository, never())
                .save(any(ExperienciaAlquiler.class));
    }

    @Test
    void debeRechazarUnaFechaInicioNula() {
        assertThrows(
                IllegalArgumentException.class,
                () -> experienciaService.registrarExperiencia(
                        inquilino.getEmail(),
                        10L,
                        null,
                        null,
                        null));

        verify(usuarioRepository, never())
                .findByEmail(any(String.class));
        verify(experienciaRepository, never())
                .save(any(ExperienciaAlquiler.class));
    }

    private void verificarEstadoNoConfirmable(
            ExperienciaAlquiler.EstadoExperiencia estado) {

        ExperienciaAlquiler experiencia = crearExperienciaPendiente();
        experiencia.setEstado(estado);

        when(usuarioRepository.findByEmail(casero.getEmail()))
                .thenReturn(Optional.of(casero));
        when(experienciaRepository.findById(1L))
                .thenReturn(Optional.of(experiencia));

        assertThrows(
                IllegalArgumentException.class,
                () -> experienciaService.confirmarExperiencia(
                        casero.getEmail(), 1L));

        verify(experienciaRepository, never())
                .save(any(ExperienciaAlquiler.class));
    }
}