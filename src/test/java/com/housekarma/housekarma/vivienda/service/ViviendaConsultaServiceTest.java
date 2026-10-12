package com.housekarma.housekarma.vivienda.service;

import com.housekarma.housekarma.vivienda.entity.Vivienda;
import com.housekarma.housekarma.vivienda.repository.ViviendaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViviendaConsultaServiceTest {

    @Mock
    private ViviendaRepository viviendaRepository;

    @InjectMocks
    private ViviendaService viviendaService;

    @Test
    void debeListarTodasLasViviendas() {
        Vivienda vivienda = new Vivienda();
        vivienda.setDireccion("Calle Corrida");
        vivienda.setNumero("10");

        when(viviendaRepository.findAll()).thenReturn(List.of(vivienda));

        List<Vivienda> resultado = viviendaRepository.findAll();

        assertEquals(1, resultado.size());
        assertEquals("Calle Corrida", resultado.get(0).getDireccion());
    }

    @Test
    void debeEncontrarViviendaPorId() {
        Vivienda vivienda = new Vivienda();
        vivienda.setDireccion("Calle Corrida");

        when(viviendaRepository.findById(1L))
                .thenReturn(Optional.of(vivienda));

        Optional<Vivienda> resultado = viviendaRepository.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Calle Corrida", resultado.get().getDireccion());
    }

    @Test
    void debeDevolverVacioCuandoLaViviendaNoExiste() {
        when(viviendaRepository.findById(99L))
                .thenReturn(Optional.empty());

        Optional<Vivienda> resultado = viviendaRepository.findById(99L);

        assertTrue(resultado.isEmpty());
    }
}
