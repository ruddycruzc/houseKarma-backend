package com.housekarma.housekarma.vivienda.service;

import com.housekarma.housekarma.vivienda.entity.Vivienda;
import com.housekarma.housekarma.vivienda.repository.ViviendaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

@ExtendWith(MockitoExtension.class)
class ViviendaServiceTest {

    @Mock
    private ViviendaRepository viviendaRepository;

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
        Vivienda vivienda = new Vivienda();
        vivienda.setDireccion("Calle Corrida");
        vivienda.setNumero("10");
        vivienda.setLocalidad("Gijón");
        vivienda.setCodigoPostal("33206");
        vivienda.setProvincia("Asturias");

        when(viviendaRepository.existsByDireccionNormalizada(
                "calle corrida, 10, gijon, 33206, asturias")).thenReturn(true);

        assertThatThrownBy(() -> viviendaService.registrar(vivienda))
                .isInstanceOf(ViviendaDuplicadaException.class)
                .hasMessageContaining("ya existe");

        verify(viviendaRepository, never()).save(any(Vivienda.class));
    }

    @Test
    void debeRegistrarViviendaConDireccionNormalizadaYFecha() {
        Vivienda vivienda = new Vivienda();
        vivienda.setDireccion("Calle Corrida");
        vivienda.setNumero("10");
        vivienda.setLocalidad("Gijón");
        vivienda.setCodigoPostal("33206");
        vivienda.setProvincia("Asturias");

        when(viviendaRepository.existsByDireccionNormalizada(
                "calle corrida, 10, gijon, 33206, asturias")).thenReturn(false);

        when(viviendaRepository.save(any(Vivienda.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Vivienda resultado = viviendaService.registrar(vivienda);

        assertThat(resultado.getDireccionNormalizada())
                .isEqualTo("calle corrida, 10, gijon, 33206, asturias");
        assertThat(resultado.getFechaRegistro()).isNotNull();

        verify(viviendaRepository).save(argThat(guardada -> guardada.getDireccionNormalizada()
                .equals("calle corrida, 10, gijon, 33206, asturias")
                && guardada.getFechaRegistro() != null));
    }

    @Test
    void noDebeRegistrarViviendaSinDireccion() {
        Vivienda vivienda = new Vivienda();
        vivienda.setDireccion(" ");
        vivienda.setNumero("10");
        vivienda.setLocalidad("Gijón");
        vivienda.setCodigoPostal("33206");
        vivienda.setProvincia("Asturias");

        assertThatThrownBy(() -> viviendaService.registrar(vivienda))
                .isInstanceOf(IllegalArgumentException.class);

        verify(viviendaRepository, never()).save(any(Vivienda.class));
    }

    @Test
    void noDebeRegistrarViviendaSinLocalidad() {
        Vivienda vivienda = new Vivienda();
        vivienda.setDireccion("Calle Corrida");
        vivienda.setNumero("10");
        vivienda.setLocalidad(" ");
        vivienda.setCodigoPostal("33206");
        vivienda.setProvincia("Asturias");

        assertThatThrownBy(() -> viviendaService.registrar(vivienda))
                .isInstanceOf(IllegalArgumentException.class);

        verify(viviendaRepository, never()).save(any(Vivienda.class));
    }

    @Test
    void noDebeRegistrarViviendaSinNumero() {
        Vivienda vivienda = crearViviendaValida();
        vivienda.setNumero(" ");

        assertThatThrownBy(() -> viviendaService.registrar(vivienda))
                .isInstanceOf(IllegalArgumentException.class);

        verify(viviendaRepository, never()).save(any(Vivienda.class));
    }

    @Test
    void noDebeRegistrarViviendaSinCodigoPostal() {
        Vivienda vivienda = crearViviendaValida();
        vivienda.setCodigoPostal(" ");

        assertThatThrownBy(() -> viviendaService.registrar(vivienda))
                .isInstanceOf(IllegalArgumentException.class);

        verify(viviendaRepository, never()).save(any(Vivienda.class));
    }

    @Test
    void noDebeRegistrarViviendaSinProvincia() {

        Vivienda vivienda = crearViviendaValida();
        vivienda.setProvincia(" ");

        assertThatThrownBy(() -> viviendaService.registrar(vivienda))
                .isInstanceOf(IllegalArgumentException.class);

        verify(viviendaRepository, never()).save(any(Vivienda.class));
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

    @Test
    void noDebeRegistrarViviendaNula() {
        assertThatThrownBy(() -> viviendaService.registrar(null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(viviendaRepository, never()).save(any(Vivienda.class));
    }
}