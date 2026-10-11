package com.housekarma.housekarma.vivienda.controller;

import com.housekarma.housekarma.vivienda.dto.ViviendaRequest;
import com.housekarma.housekarma.vivienda.dto.ViviendaResponse;
import com.housekarma.housekarma.vivienda.entity.Vivienda;
import com.housekarma.housekarma.vivienda.service.ViviendaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/viviendas")
public class ViviendaController {

    private final ViviendaService viviendaService;

    public ViviendaController(ViviendaService viviendaService) {
        this.viviendaService = viviendaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ViviendaResponse registrar(@Valid @RequestBody ViviendaRequest request) {
        Vivienda vivienda = new Vivienda();
        vivienda.setDireccion(request.direccion());
        vivienda.setNumero(request.numero());
        vivienda.setComplemento(request.complemento());
        vivienda.setLocalidad(request.localidad());
        vivienda.setCodigoPostal(request.codigoPostal());
        vivienda.setProvincia(request.provincia());

        Vivienda registrada = viviendaService.registrar(vivienda);

        return ViviendaResponse.fromEntity(registrada);
    }
}