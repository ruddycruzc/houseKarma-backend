package com.housekarma.housekarma.vivienda.service;

import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;
import com.housekarma.housekarma.vivienda.entity.Vivienda;
import com.housekarma.housekarma.vivienda.repository.ViviendaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViviendaServiceTest {

    private static final String EMAIL_CASERO = "casero@example.com";

    @Mock
    private ViviendaRepository viviendaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ViviendaService viviendaService;

    @Test
    void debeNormalizarDireccionEliminandoTildesYMayusculas() {
        String direccion = "  CÁLLE Corrida 10, Gijón  ";

        String resultado = viviendaService.normalizarDireccion(direccion);

        assertThat(resultado).isEqualTo("calle corrida 10, gijon");
    }

    @Test
    void noDebeRegistrarUnaDireccionDuplicada() {
        Vivienda vivienda = crearViviendaValida();
        prepararCasero();

        when(viviendaRepository.existsByDireccionNormalizada(
                "calle corrida, 10, gijon, 33206, asturias"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                viviendaService.registrar(vivienda, EMAIL_CASERO))
                .isInstanceOf(ViviendaDuplicadaException.class)
                .hasMessageContaining("ya existe");

        verify(viviendaRepository, never()).save(any(Vivienda.class));
    }

    @Test
    void debeRegistrarViviendaConDireccionNormalizadaYFecha() {
        Vivienda vivienda = crearViviendaValida();
        prepararCasero();

        when(viviendaRepository.existsByDireccionNormalizada(
                "calle corrida, 10, gijon, 33206, asturias"))
                .thenReturn(false);

        when(viviendaRepository.save(any(Vivienda.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Vivienda resultado =
                viviendaService.registrar(vivienda, EMAIL_CASERO);

        assertThat(resultado.getDireccionNormalizada())
                .isEqualTo("calle corrida, 10, gijon, 33206, asturias");
        assertThat(resultado.getFechaRegistro()).isNotNull();
        assertThat(resultado.getCasero().getEmail())
                .isEqualTo(EMAIL_CASERO);

        verify(viviendaRepository).save(argThat(guardada ->
                guardada.getDireccionNormalizada()
                        .equals("calle corrida, 10, gijon, 33206, asturias")
                && guardada.getFechaRegistro() != null
                && guardada.getCasero().getEmail().equals(EMAIL_CASERO)
        ));
    }

    @Test
    void noDebeRegistrarViviendaSinDireccion() {
        Vivienda vivienda = crearViviendaValida();
        vivienda.setDireccion(" ");

        assertThatThrownBy(() ->
                viviendaService.registrar(vivienda, EMAIL_CASERO))
                .isInstanceOf(IllegalArgumentException.class);

        verify(viviendaRepository, never()).save(any(Vivienda.class));
    }

    @Test
    void noDebeRegistrarViviendaSinLocalidad() {
        Vivienda vivienda = crearViviendaValida();
        vivienda.setLocalidad(" ");

        assertThatThrownBy(() ->
                viviendaService.registrar(vivienda, EMAIL_CASERO))
                .isInstanceOf(IllegalArgumentException.class);

        verify(viviendaRepository, never()).save(any(Vivienda.class));
    }

    @Test
    void noDebeRegistrarViviendaSinNumero() {
        Vivienda vivienda = crearViviendaValida();
        vivienda.setNumero(" ");

        assertThatThrownBy(() ->
                viviendaService.registrar(vivienda, EMAIL_CASERO))
                .isInstanceOf(IllegalArgumentException.class);

        verify(viviendaRepository, never()).save(any(Vivienda.class));
    }

    @Test
    void noDebeRegistrarViviendaSinCodigoPostal() {
        Vivienda vivienda = crearViviendaValida();
        vivienda.setCodigoPostal(" ");

        assertThatThrownBy(() ->
                viviendaService.registrar(vivienda, EMAIL_CASERO))
                .isInstanceOf(IllegalArgumentException.class);

        verify(viviendaRepository, never()).save(any(Vivienda.class));
    }

    @Test
    void noDebeRegistrarViviendaSinProvincia() {
        Vivienda vivienda = crearViviendaValida();
        vivienda.setProvincia(" ");

        assertThatThrownBy(() ->
                viviendaService.registrar(vivienda, EMAIL_CASERO))
                .isInstanceOf(IllegalArgumentException.class);

        verify(viviendaRepository, never()).save(any(Vivienda.class));
    }

    @Test
    void noDebeRegistrarViviendaNula() {
        assertThatThrownBy(() ->
                viviendaService.registrar(null, EMAIL_CASERO))
                .isInstanceOf(IllegalArgumentException.class);

        verify(viviendaRepository, never()).save(any(Vivienda.class));
    }

    @Test
    void noDebeRegistrarViviendaSiElUsuarioNoEsCasero() {
        Vivienda vivienda = crearViviendaValida();
        Usuario inquilino = new Usuario();
        inquilino.setEmail("inquilino@example.com");
        inquilino.setRol(Rol.INQUILINO);

        when(usuarioRepository.findByEmail("inquilino@example.com"))
                .thenReturn(Optional.of(inquilino));

        assertThatThrownBy(() ->
                viviendaService.registrar(vivienda, "inquilino@example.com"))
                .isInstanceOf(org.springframework.web.server.ResponseStatusException.class)
                .hasMessageContaining("Solo un casero");

        verify(viviendaRepository, never()).save(any(Vivienda.class));
    }

    @Test
    void noDebeRegistrarViviendaSiElUsuarioNoExiste() {
        Vivienda vivienda = crearViviendaValida();

        when(usuarioRepository.findByEmail(EMAIL_CASERO))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                viviendaService.registrar(vivienda, EMAIL_CASERO))
                .isInstanceOf(org.springframework.web.server.ResponseStatusException.class)
                .hasMessageContaining("No se ha encontrado");

        verify(viviendaRepository, never()).save(any(Vivienda.class));
    }

    private void prepararCasero() {
        Usuario casero = new Usuario();
        casero.setEmail(EMAIL_CASERO);
        casero.setRol(Rol.CASERO);

        when(usuarioRepository.findByEmail(EMAIL_CASERO))
                .thenReturn(Optional.of(casero));
    }

    private Vivienda crearViviendaValida() {
        Vivienda vivienda = new Vivienda();
        vivienda.setDireccion("Calle Corrida");
        vivienda.setNumero("10");
        vivienda.setLocalidad("Gijón");
        vivienda.setCodigoPostal("33206");
        vivienda.setProvincia("Asturias");
        return vivienda;
    }
}