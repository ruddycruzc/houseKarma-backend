package com.housekarma.housekarma.valoracion.controller;

import com.housekarma.housekarma.valoracion.dto.ValoracionRequest;
import com.housekarma.housekarma.valoracion.dto.ValoracionResponse;
import com.housekarma.housekarma.valoracion.service.ValoracionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/valoraciones")
public class ValoracionController {

    private final ValoracionService valoracionService;

    public ValoracionController(ValoracionService valoracionService) {
        this.valoracionService = valoracionService;
    }

    @PostMapping("/experiencias/{experienciaId}")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('INQUILINO', 'CASERO')")
    public ValoracionResponse registrar(
            @PathVariable Long experienciaId,
            @Valid @RequestBody ValoracionRequest request,
            Authentication authentication) {

        return valoracionService.registrar(
                authentication.getName(),
                experienciaId,
                request
        );
    }

    @GetMapping("/usuarios/{usuarioId}")
    public List<ValoracionResponse> obtenerValoraciones(
            @PathVariable Long usuarioId) {

        return valoracionService.obtenerValoraciones(usuarioId);
    }

    @GetMapping("/usuarios/{usuarioId}/media")
    public Double obtenerMedia(@PathVariable Long usuarioId) {
        return valoracionService.obtenerMedia(usuarioId);
    }

    @GetMapping("/usuarios/{usuarioId}/total")
    public long contarValoraciones(@PathVariable Long usuarioId) {
        return valoracionService.contarValoraciones(usuarioId);
    }
}