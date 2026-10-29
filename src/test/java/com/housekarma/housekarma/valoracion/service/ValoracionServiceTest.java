package com.housekarma.housekarma.valoracion.service;

import com.housekarma.housekarma.experiencia.entity.ExperienciaAlquiler;
import com.housekarma.housekarma.experiencia.repository.ExperienciaAlquilerRepository;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;
import com.housekarma.housekarma.valoracion.dto.ValoracionCriterioRequest;
import com.housekarma.housekarma.valoracion.dto.ValoracionRequest;
import com.housekarma.housekarma.valoracion.dto.ValoracionResponse;
import com.housekarma.housekarma.valoracion.entity.Valoracion;
import com.housekarma.housekarma.valoracion.repository.ValoracionCriterioRepository;
import com.housekarma.housekarma.valoracion.repository.ValoracionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValoracionServiceTest {

        @Mock
        private ValoracionRepository valoracionRepository;

        @Mock
        private ExperienciaAlquilerRepository experienciaRepository;

        @Mock
        private UsuarioRepository usuarioRepository;
        @Mock
        private ValoracionCriterioRepository criterioRepository;

        @InjectMocks
        private ValoracionService valoracionService;

        private Usuario inquilino;
        private Usuario casero;
        private ExperienciaAlquiler experiencia;

        @BeforeEach
        void setUp() {
                inquilino = mock(Usuario.class);
                casero = mock(Usuario.class);
                experiencia = mock(ExperienciaAlquiler.class);
                valoracionService = new ValoracionService(
                                valoracionRepository,
                                criterioRepository,
                                experienciaRepository,
                                usuarioRepository);
        }

        @Test
        void debeRegistrarValoracionCuandoExperienciaEstaFinalizada() {
                prepararExperienciaFinalizada();

                when(usuarioRepository.findByEmail("inquilino@test.com"))
                                .thenReturn(Optional.of(inquilino));
                when(valoracionRepository.existsByExperienciaIdAndAutorId(10L, 1L))
                                .thenReturn(false);
                when(valoracionRepository.save(any(Valoracion.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                ValoracionResponse response = valoracionService.registrar(
                                "inquilino@test.com",
                                10L,
                                new ValoracionRequest(
                                                List.of(
                                                                new ValoracionCriterioRequest("COMUNICACION", 5),
                                                                new ValoracionCriterioRequest("TRANSPARENCIA", 4),
                                                                new ValoracionCriterioRequest("MANTENIMIENTO", 5),
                                                                new ValoracionCriterioRequest("FIANZA", 4),
                                                                new ValoracionCriterioRequest("RESPETO", 5)),
                                                "Comentario de prueba"));

                assertEquals(5, response.puntuacion());
                assertEquals(1L, response.autorId());
                assertEquals(2L, response.destinatarioId());
                verify(valoracionRepository).save(any(Valoracion.class));
        }

        @Test
        void noDebeValorarExperienciaNoFinalizada() {
                when(usuarioRepository.findByEmail("inquilino@test.com"))
                                .thenReturn(Optional.of(inquilino));
                when(experienciaRepository.findById(10L))
                                .thenReturn(Optional.of(experiencia));
                when(experiencia.getEstado())
                                .thenReturn(ExperienciaAlquiler.EstadoExperiencia.CONFIRMADA);

                assertThrows(IllegalArgumentException.class, () -> valoracionService.registrar(
                                "inquilino@test.com",
                                10L,
                                new ValoracionRequest(
                                                List.of(
                                                                new ValoracionCriterioRequest("COMUNICACION", 5),
                                                                new ValoracionCriterioRequest("TRANSPARENCIA", 4),
                                                                new ValoracionCriterioRequest("MANTENIMIENTO", 5),
                                                                new ValoracionCriterioRequest("FIANZA", 4),
                                                                new ValoracionCriterioRequest("RESPETO", 5)),
                                                "Comentario de prueba")));

                verify(valoracionRepository, never()).save(any());
        }

        @Test
        void noDebePermitirValorarSiNoEsParticipante() {
                Usuario otroUsuario = mock(Usuario.class);

                when(usuarioRepository.findByEmail("otro@test.com"))
                                .thenReturn(Optional.of(otroUsuario));
                when(experienciaRepository.findById(10L))
                                .thenReturn(Optional.of(experiencia));
                when(experiencia.getEstado())
                                .thenReturn(ExperienciaAlquiler.EstadoExperiencia.FINALIZADA);
                when(otroUsuario.getId()).thenReturn(3L);
                when(inquilino.getId()).thenReturn(1L);
                when(casero.getId()).thenReturn(2L);
                when(experiencia.getInquilino()).thenReturn(inquilino);
                when(experiencia.getCasero()).thenReturn(casero);

                assertThrows(IllegalArgumentException.class, () -> valoracionService.registrar(
                                "otro@test.com",
                                10L,
                                new ValoracionRequest(
                                                List.of(
                                                                new ValoracionCriterioRequest("COMUNICACION", 5),
                                                                new ValoracionCriterioRequest("TRANSPARENCIA", 4),
                                                                new ValoracionCriterioRequest("MANTENIMIENTO", 5),
                                                                new ValoracionCriterioRequest("FIANZA", 4),
                                                                new ValoracionCriterioRequest("RESPETO", 5)),
                                                "Comentario de prueba")));

                verify(valoracionRepository, never()).save(any());
                verify(valoracionRepository, never())
                                .existsByExperienciaIdAndAutorId(anyLong(), anyLong());
        }

        @Test
        void noDebePermitirValorarDosVecesLaMismaExperiencia() {
                when(usuarioRepository.findByEmail("inquilino@test.com"))
                                .thenReturn(Optional.of(inquilino));
                when(experienciaRepository.findById(10L))
                                .thenReturn(Optional.of(experiencia));

                when(experiencia.getEstado())
                                .thenReturn(ExperienciaAlquiler.EstadoExperiencia.FINALIZADA);
                when(experiencia.getInquilino()).thenReturn(inquilino);
                when(experiencia.getCasero()).thenReturn(casero);

                when(inquilino.getId()).thenReturn(1L);
                when(casero.getId()).thenReturn(2L);

                when(valoracionRepository.existsByExperienciaIdAndAutorId(10L, 1L))
                                .thenReturn(true);

                assertThrows(IllegalArgumentException.class, () -> valoracionService.registrar(
                                "inquilino@test.com",
                                10L,
                                new ValoracionRequest(
                                                List.of(
                                                                new ValoracionCriterioRequest("COMUNICACION", 5),
                                                                new ValoracionCriterioRequest("TRANSPARENCIA", 4),
                                                                new ValoracionCriterioRequest("MANTENIMIENTO", 5),
                                                                new ValoracionCriterioRequest("FIANZA", 4),
                                                                new ValoracionCriterioRequest("RESPETO", 5)),
                                                "Comentario de prueba")));

                verify(valoracionRepository)
                                .existsByExperienciaIdAndAutorId(10L, 1L);
                verify(valoracionRepository, never()).save(any());
        }

        @Test
        void noDebeAceptarPuntuacionFueraDeRango() {
                ValoracionRequest request = new ValoracionRequest(
                                List.of(
                                                new ValoracionCriterioRequest("COMUNICACION", 6),
                                                new ValoracionCriterioRequest("TRANSPARENCIA", 4),
                                                new ValoracionCriterioRequest("MANTENIMIENTO", 5),
                                                new ValoracionCriterioRequest("FIANZA", 4),
                                                new ValoracionCriterioRequest("RESPETO", 5)),
                                "Comentario de prueba");

                assertThrows(
                                IllegalArgumentException.class,
                                () -> valoracionService.registrar(
                                                "inquilino@test.com",
                                                10L,
                                                request));

                verify(usuarioRepository, never()).findByEmail(anyString());
                verify(valoracionRepository, never()).save(any());
        }

        private void prepararExperienciaFinalizada() {
                when(experienciaRepository.findById(10L))
                                .thenReturn(Optional.of(experiencia));
                when(experiencia.getEstado())
                                .thenReturn(ExperienciaAlquiler.EstadoExperiencia.FINALIZADA);
                when(experiencia.getInquilino()).thenReturn(inquilino);
                when(experiencia.getCasero()).thenReturn(casero);
                when(experiencia.getId()).thenReturn(10L);
                when(inquilino.getId()).thenReturn(1L);
                when(casero.getId()).thenReturn(2L);
        }
}