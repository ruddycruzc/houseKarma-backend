package com.housekarma.housekarma.experiencia.controller;

import com.housekarma.housekarma.experiencia.dto.ExperienciaAlquilerRequest;
import com.housekarma.housekarma.experiencia.dto.ExperienciaAlquilerResponse;
import com.housekarma.housekarma.experiencia.service.ExperienciaAlquilerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import com.housekarma.housekarma.experiencia.dto.ExperienciaAlquilerPendienteResponse;
import java.util.List;

@RestController
@RequestMapping("/api/experiencias")
@PreAuthorize("hasAnyRole('INQUILINO', 'CASERO')")
public class ExperienciaAlquilerController {

    private final ExperienciaAlquilerService experienciaService;

    public ExperienciaAlquilerController(
            ExperienciaAlquilerService experienciaService) {
        this.experienciaService = experienciaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExperienciaAlquilerResponse registrar(
            @Valid @RequestBody ExperienciaAlquilerRequest request,
            Authentication authentication) {

        var experiencia = experienciaService.registrarExperiencia(
                authentication.getName(),
                request.viviendaId(),
                request.inquilinoId(),
                request.fechaInicio(),
                request.fechaFin());

        return ExperienciaAlquilerResponse.desde(experiencia);
    }

    @PatchMapping("/{id}/confirmacion")
    public ExperienciaAlquilerResponse confirmar(
            @PathVariable Long id,
            Authentication authentication) {

        var experiencia = experienciaService.confirmarExperiencia(
                authentication.getName(),
                id);

        return ExperienciaAlquilerResponse.desde(experiencia);
    }

    @GetMapping("/pendientes/casero")
    @PreAuthorize("hasRole('CASERO')")
    public List<ExperienciaAlquilerPendienteResponse> obtenerPendientesCasero(
            Authentication authentication) {

        return experienciaService.obtenerPendientesCasero(
                authentication.getName());
    }
}