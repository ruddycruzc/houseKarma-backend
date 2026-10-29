package com.housekarma.housekarma.valoracion.service;

import com.housekarma.housekarma.experiencia.entity.ExperienciaAlquiler;
import com.housekarma.housekarma.experiencia.repository.ExperienciaAlquilerRepository;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;
import com.housekarma.housekarma.valoracion.dto.ValoracionCriterioRequest;
import com.housekarma.housekarma.valoracion.dto.ValoracionRequest;
import com.housekarma.housekarma.valoracion.dto.ValoracionResponse;
import com.housekarma.housekarma.valoracion.entity.Valoracion;
import com.housekarma.housekarma.valoracion.entity.ValoracionCriterio;
import com.housekarma.housekarma.valoracion.repository.ValoracionCriterioRepository;
import com.housekarma.housekarma.valoracion.repository.ValoracionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ValoracionService {

    private final ValoracionRepository valoracionRepository;
    private final ValoracionCriterioRepository criterioRepository;
    private final ExperienciaAlquilerRepository experienciaRepository;
    private final UsuarioRepository usuarioRepository;

    public ValoracionService(
        ValoracionRepository valoracionRepository,
        ValoracionCriterioRepository criterioRepository,
        ExperienciaAlquilerRepository experienciaRepository,
        UsuarioRepository usuarioRepository
    ) {
        this.valoracionRepository = valoracionRepository;
        this.criterioRepository = criterioRepository;
        this.experienciaRepository = experienciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ValoracionResponse registrar(
        String emailAutor,
        Long experienciaId,
        ValoracionRequest request
    ) {
        // 1. Validar estructura y puntuaciones antes de consultar repositorios.
        validarPuntuaciones(request);

        // 2. Obtener el usuario autenticado.
        Usuario autor = usuarioRepository.findByEmail(emailAutor)
            .orElseThrow(() -> new IllegalArgumentException(
                "No se ha encontrado el usuario autenticado"
            ));

        // 3. Obtener la experiencia.
        ExperienciaAlquiler experiencia = experienciaRepository.findById(experienciaId)
            .orElseThrow(() -> new IllegalArgumentException(
                "No se ha encontrado la experiencia"
            ));

        // 4. Solo se pueden valorar experiencias finalizadas.
        if (experiencia.getEstado()
                != ExperienciaAlquiler.EstadoExperiencia.FINALIZADA) {
            throw new IllegalArgumentException(
                "Solo se pueden valorar experiencias finalizadas"
            );
        }

        Long autorId = autor.getId();
        Long inquilinoId = experiencia.getInquilino().getId();
        Long caseroId = experiencia.getCasero().getId();

        // 5. Determinar destinatario y verificar participación.
        Usuario destinatario;
        boolean autorEsInquilino;

        if (autorId.equals(inquilinoId)) {
            destinatario = experiencia.getCasero();
            autorEsInquilino = true;
        } else if (autorId.equals(caseroId)) {
            destinatario = experiencia.getInquilino();
            autorEsInquilino = false;
        } else {
            throw new IllegalArgumentException(
                "Solo los participantes pueden valorar esta experiencia"
            );
        }

        // 6. Comprobar que los criterios corresponden al rol.
        validarCriterios(request, autorEsInquilino);

        // 7. Evitar valoraciones duplicadas.
        if (valoracionRepository.existsByExperienciaIdAndAutorId(
                experienciaId, autorId)) {
            throw new IllegalArgumentException(
                "Ya has valorado esta experiencia"
            );
        }

        // 8. Calcular la puntuación general.
        int suma = request.criterios().stream()
            .mapToInt(criterio -> criterio.puntuacion())
            .sum();

        int puntuacionGeneral = (int) Math.round(
            suma / (double) request.criterios().size()
        );

        // 9. Guardar la valoración principal.
        Valoracion valoracion = new Valoracion();
        valoracion.setExperiencia(experiencia);
        valoracion.setAutor(autor);
        valoracion.setDestinatario(destinatario);
        valoracion.setPuntuacion(puntuacionGeneral);
        valoracion.setComentario(request.comentario());

        Valoracion valoracionGuardada = valoracionRepository.save(valoracion);

        // 10. Guardar los cinco criterios asociados.
        List<ValoracionCriterio> criterios = request.criterios().stream()
            .map(criterio -> new ValoracionCriterio(
                valoracionGuardada,
                criterio.criterio(),
                criterio.puntuacion()
            ))
            .toList();

        criterioRepository.saveAll(criterios);

        return toResponse(valoracionGuardada);
    }

    private void validarPuntuaciones(ValoracionRequest request) {
        if (request == null
                || request.criterios() == null
                || request.criterios().size() != 5) {
            throw new IllegalArgumentException(
                "Debes valorar los cinco criterios"
            );
        }

        Set<String> recibidos = new HashSet<>();

        for (ValoracionCriterioRequest criterio : request.criterios()) {
            if (criterio == null
                    || criterio.criterio() == null
                    || criterio.puntuacion() == null) {
                throw new IllegalArgumentException(
                    "Todos los criterios deben incluir nombre y puntuación"
                );
            }

            if (criterio.puntuacion() < 1 || criterio.puntuacion() > 5) {
                throw new IllegalArgumentException(
                    "Cada puntuación debe estar entre 1 y 5"
                );
            }

            if (!recibidos.add(criterio.criterio())) {
                throw new IllegalArgumentException(
                    "No puedes repetir un criterio"
                );
            }
        }
    }

    private void validarCriterios(
        ValoracionRequest request,
        boolean autorEsInquilino
    ) {
        Set<String> esperados = autorEsInquilino
            ? Set.of(
                "COMUNICACION",
                "TRANSPARENCIA",
                "MANTENIMIENTO",
                "FIANZA",
                "RESPETO"
            )
            : Set.of(
                "COMUNICACION",
                "CUIDADO_VIVIENDA",
                "CUMPLIMIENTO_ACUERDOS",
                "GESTION_INCIDENCIAS",
                "RESPETO"
            );

        Set<String> recibidos = new HashSet<>();

        request.criterios().forEach(
            criterio -> recibidos.add(criterio.criterio())
        );

        if (!recibidos.equals(esperados)) {
            throw new IllegalArgumentException(
                "Los criterios enviados no corresponden a esta valoración"
            );
        }
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