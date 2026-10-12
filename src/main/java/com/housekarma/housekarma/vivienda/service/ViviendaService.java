package com.housekarma.housekarma.vivienda.service;

import com.housekarma.housekarma.vivienda.entity.Vivienda;
import com.housekarma.housekarma.vivienda.repository.ViviendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class ViviendaService {

    private final ViviendaRepository viviendaRepository;

    public ViviendaService(ViviendaRepository viviendaRepository) {
        this.viviendaRepository = viviendaRepository;
    }

    public String normalizarDireccion(String direccion) {
        String sinTildes = Normalizer.normalize(
                direccion.trim(),
                Normalizer.Form.NFD).replaceAll("\\p{M}", "");

        return sinTildes
                .replaceAll("\\s+", " ")
                .toLowerCase(Locale.ROOT);
    }

    @Transactional
    public Vivienda registrar(Vivienda vivienda) {
        validarVivienda(vivienda);
        String direccionCompleta = String.join(", ",
                vivienda.getDireccion().trim(),
                vivienda.getNumero().trim(),
                normalizarDireccion(vivienda.getLocalidad()),
                vivienda.getCodigoPostal().trim(),
                normalizarDireccion(vivienda.getProvincia()));

        String direccionNormalizada = normalizarDireccion(direccionCompleta);

        if (viviendaRepository.existsByDireccionNormalizada(direccionNormalizada)) {
            throw new ViviendaDuplicadaException(
                    "La vivienda con esa dirección ya existe");
        }

        vivienda.setDireccionNormalizada(direccionNormalizada);
        vivienda.setFechaRegistro(LocalDateTime.now());

        return viviendaRepository.save(vivienda);
    }

    private void validarVivienda(Vivienda vivienda) {
        if (vivienda == null) {
            throw new IllegalArgumentException(
                    "La vivienda no puede ser nula");
        }

        if (vivienda.getDireccion() == null
                || vivienda.getDireccion().isBlank()) {
            throw new IllegalArgumentException(
                    "La dirección es obligatoria");
        }

        if (vivienda.getNumero() == null
                || vivienda.getNumero().isBlank()) {
            throw new IllegalArgumentException(
                    "El número es obligatorio");
        }

        if (vivienda.getLocalidad() == null
                || vivienda.getLocalidad().isBlank()) {
            throw new IllegalArgumentException(
                    "La localidad es obligatoria");
        }

        if (vivienda.getCodigoPostal() == null
                || vivienda.getCodigoPostal().isBlank()) {
            throw new IllegalArgumentException(
                    "El código postal es obligatorio");
        }

        if (vivienda.getProvincia() == null
                || vivienda.getProvincia().isBlank()) {
            throw new IllegalArgumentException(
                    "La provincia es obligatoria");
        }
    }

    @Transactional(readOnly = true)
    public List<Vivienda> listarTodas() {
        return viviendaRepository.findAll();
    }

@Transactional(readOnly = true)
public Vivienda buscarPorId(Long id) {
    return viviendaRepository.findById(id)
            .orElseThrow(() -> new ViviendaNoEncontradaException(
                    "No se ha encontrado la vivienda con ID: " + id));
}
}
