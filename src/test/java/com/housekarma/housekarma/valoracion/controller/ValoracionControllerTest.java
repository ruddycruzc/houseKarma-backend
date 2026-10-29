package com.housekarma.housekarma.valoracion.controller;

import com.housekarma.housekarma.valoracion.dto.ValoracionRequest;
import com.housekarma.housekarma.valoracion.dto.ValoracionResponse;
import com.housekarma.housekarma.valoracion.dto.ValoracionCriterioRequest;
import com.housekarma.housekarma.valoracion.service.ValoracionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValoracionControllerTest {

        @Mock
        private ValoracionService valoracionService;

        @Mock
        private Authentication authentication;

        private ValoracionController controller;

        @BeforeEach
        void setUp() {
                controller = new ValoracionController(valoracionService);
        }

        @Test
        void debeRegistrarValoracionConElUsuarioAutenticado() {
                when(authentication.getName())
                                .thenReturn("inquilino@test.com");

                ValoracionRequest request = new ValoracionRequest(
                                List.of(
                                                new ValoracionCriterioRequest("COMUNICACION", 5),
                                                new ValoracionCriterioRequest("TRANSPARENCIA", 4),
                                                new ValoracionCriterioRequest("MANTENIMIENTO", 5),
                                                new ValoracionCriterioRequest("FIANZA", 4),
                                                new ValoracionCriterioRequest("RESPETO", 5)),
                                "Comentario de prueba");

                controller.registrar(10L, request, authentication);

                verify(valoracionService).registrar(
                                "inquilino@test.com",
                                10L,
                                request);
        }

        @Test
        void debeObtenerValoracionesDeUnUsuario() {
                when(valoracionService.obtenerValoraciones(2L))
                                .thenReturn(List.of());

                List<ValoracionResponse> resultado = controller.obtenerValoraciones(2L);

                assertEquals(0, resultado.size());
                verify(valoracionService).obtenerValoraciones(2L);
        }

        @Test
        void debeObtenerMediaDeUnUsuario() {
                when(valoracionService.obtenerMedia(2L))
                                .thenReturn(4.5);

                Double resultado = controller.obtenerMedia(2L);

                assertEquals(4.5, resultado);
                verify(valoracionService).obtenerMedia(2L);
        }

        @Test
        void debeContarValoracionesDeUnUsuario() {
                when(valoracionService.contarValoraciones(2L))
                                .thenReturn(3L);

                long resultado = controller.contarValoraciones(2L);

                assertEquals(3L, resultado);
                verify(valoracionService).contarValoraciones(2L);
        }
}