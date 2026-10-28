package com.housekarma.housekarma.valoracion.service;

import com.housekarma.housekarma.experiencia.entity.ExperienciaAlquiler;
import com.housekarma.housekarma.experiencia.repository.ExperienciaAlquilerRepository;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;
import com.housekarma.housekarma.valoracion.dto.ValoracionRequest;
import com.housekarma.housekarma.valoracion.dto.ValoracionResponse;
import com.housekarma.housekarma.valoracion.entity.Valoracion;
import com.housekarma.housekarma.valoracion.repository.ValoracionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ValoracionService {

    private final ValoracionRepository valoracionRepository;
    private final ExperienciaAlquilerRepository experienciaRepository;
    private final UsuarioRepository usuarioRepository;

    public ValoracionService(
        ValoracionRepository valoracionRepository,
        ExperienciaAlquilerRepository experienciaRepository,
        UsuarioRepository usuarioRepository
    ) {
        this.valoracionRepository = valoracionRepository;
        this.experienciaRepository = experienciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ValoracionResponse registrar(
        String emailAutor,
        Long experienciaId,
        ValoracionRequest request
    ) {
        validarPuntuacion(request.puntuacion());

        Usuario autor = usuarioRepository.findByEmail(emailAutor)
            .orElseThrow(() -> new IllegalArgumentException(
                "No se ha encontrado el usuario autenticado"
            ));

        ExperienciaAlquiler experiencia = experienciaRepository.findById(experienciaId)
            .orElseThrow(() -> new IllegalArgumentException(
                "No se ha encontrado la experiencia"
            ));

        if (experiencia.getEstado()
                != ExperienciaAlquiler.EstadoExperiencia.FINALIZADA) {
            throw new IllegalArgumentException(
                "Solo se pueden valorar experiencias finalizadas"
            );
        }

        Long autorId = autor.getId();
        Long inquilinoId = experiencia.getInquilino().getId();
        Long caseroId = experiencia.getCasero().getId();

        Usuario destinatario;

        if (autorId.equals(inquilinoId)) {
            destinatario = experiencia.getCasero();
        } else if (autorId.equals(caseroId)) {
            destinatario = experiencia.getInquilino();
        } else {
            throw new IllegalArgumentException(
                "Solo los participantes pueden valorar esta experiencia"
            );
        }

        if (valoracionRepository.existsByExperienciaIdAndAutorId(
                experienciaId, autorId)) {
            throw new IllegalArgumentException(
                "Ya has valorado esta experiencia"
            );
        }

        Valoracion valoracion = new Valoracion();
        valoracion.setExperiencia(experiencia);
        valoracion.setAutor(autor);
        valoracion.setDestinatario(destinatario);
        valoracion.setPuntuacion(request.puntuacion());
        valoracion.setComentario(request.comentario());

        return toResponse(valoracionRepository.save(valoracion));
    }

    @Transactional(readOnly = true)
    public Double obtenerMedia(Long usuarioId) {
        return valoracionRepository.calcularMediaRecibida(usuarioId);
    }

    @Transactional(readOnly = true)
    public long contarValoraciones(Long usuarioId) {
        return valoracionRepository.countByDestinatarioId(usuarioId);
    }

    @Transactional(readOnly = true)
    public List<ValoracionResponse> obtenerValoraciones(Long usuarioId) {
        return valoracionRepository
            .findAllByDestinatarioIdOrderByFechaRegistroDesc(usuarioId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private void validarPuntuacion(Integer puntuacion) {
        if (puntuacion == null || puntuacion < 1 || puntuacion > 5) {
            throw new IllegalArgumentException(
                "La puntuación debe estar entre 1 y 5"
            );
        }
    }

    private ValoracionResponse toResponse(Valoracion valoracion) {
        return new ValoracionResponse(
            valoracion.getId(),
            valoracion.getExperiencia().getId(),
            valoracion.getAutor().getId(),
            nombreCompleto(valoracion.getAutor()),
            valoracion.getDestinatario().getId(),
            nombreCompleto(valoracion.getDestinatario()),
            valoracion.getPuntuacion(),
            valoracion.getComentario(),
            valoracion.getFechaRegistro()
        );
    }

    private String nombreCompleto(Usuario usuario) {
        return usuario.getNombre() + " " + usuario.getApellidos();
    }
}