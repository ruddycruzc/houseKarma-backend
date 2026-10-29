package com.housekarma.housekarma.vivienda.controller;

import com.housekarma.housekarma.vivienda.dto.ViviendaRequest;
import com.housekarma.housekarma.vivienda.dto.ViviendaResponse;
import com.housekarma.housekarma.vivienda.entity.Vivienda;
import com.housekarma.housekarma.vivienda.service.ViviendaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/viviendas")
public class ViviendaController {

    private final ViviendaService viviendaService;

    public ViviendaController(ViviendaService viviendaService) {
        this.viviendaService = viviendaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ViviendaResponse registrar(
            @Valid @RequestBody ViviendaRequest request,
            Authentication authentication) {

        Vivienda vivienda = new Vivienda();
        vivienda.setDireccion(request.direccion());
        vivienda.setNumero(request.numero());
        vivienda.setComplemento(request.complemento());
        vivienda.setLocalidad(request.localidad());
        vivienda.setCodigoPostal(request.codigoPostal());
        vivienda.setProvincia(request.provincia());

        Vivienda registrada = viviendaService.registrar(
                vivienda,
                authentication.getName());

        return ViviendaResponse.fromEntity(registrada);
    }

    @GetMapping
    public ResponseEntity<List<ViviendaResponse>> listarTodas() {
        List<ViviendaResponse> viviendas = viviendaService.listarTodas()
                .stream()
                .map(ViviendaResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(viviendas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViviendaResponse> buscarPorId(
            @PathVariable Long id) {
        Vivienda vivienda = viviendaService.buscarPorId(id);

        return ResponseEntity.ok(ViviendaResponse.fromEntity(vivienda));
    }

    @GetMapping("/mis-viviendas")
    public ResponseEntity<List<ViviendaResponse>> listarMisViviendas(
            Authentication authentication) {

        List<ViviendaResponse> viviendas = viviendaService
                .listarPorCasero(authentication.getName())
                .stream()
                .map(ViviendaResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(viviendas);
    }
}