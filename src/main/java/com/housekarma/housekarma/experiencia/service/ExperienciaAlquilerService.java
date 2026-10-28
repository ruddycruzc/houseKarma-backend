package com.housekarma.housekarma.experiencia.service;

import com.housekarma.housekarma.experiencia.entity.ExperienciaAlquiler;
import com.housekarma.housekarma.experiencia.repository.ExperienciaAlquilerRepository;
import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;
import com.housekarma.housekarma.vivienda.entity.Vivienda;
import com.housekarma.housekarma.vivienda.repository.ViviendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import com.housekarma.housekarma.experiencia.dto.ExperienciaAlquilerListadoResponse;
import com.housekarma.housekarma.experiencia.dto.ExperienciaAlquilerPendienteResponse;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ExperienciaAlquilerService {

        private final ExperienciaAlquilerRepository experienciaRepository;
        private final UsuarioRepository usuarioRepository;
        private final ViviendaRepository viviendaRepository;

        public ExperienciaAlquilerService(
                        ExperienciaAlquilerRepository experienciaRepository,
                        UsuarioRepository usuarioRepository,
                        ViviendaRepository viviendaRepository) {
                this.experienciaRepository = experienciaRepository;
                this.usuarioRepository = usuarioRepository;
                this.viviendaRepository = viviendaRepository;
        }

        @Transactional
        public ExperienciaAlquiler registrarExperiencia(
                        String emailIniciador,
                        Long viviendaId,
                        Long inquilinoId,
                        LocalDate fechaInicio,
                        LocalDate fechaFin) {

                validarFechas(fechaInicio, fechaFin);

                Usuario iniciador = usuarioRepository.findByEmail(emailIniciador)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No se ha encontrado al usuario autenticado"));

                Vivienda vivienda = viviendaRepository.findById(viviendaId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No se ha encontrado la vivienda"));

                Usuario casero = vivienda.getCasero();
                Usuario inquilino;

                if (iniciador.getRol() == Rol.INQUILINO) {
                        if (inquilinoId != null) {
                                throw new IllegalArgumentException(
                                                "El inquilino iniciador no debe indicar otro inquilino");
                        }
                        inquilino = iniciador;
                } else if (iniciador.getRol() == Rol.CASERO) {
                        if (!iniciador.getId().equals(casero.getId())) {
                                throw new IllegalArgumentException(
                                                "El casero iniciador no es propietario de la vivienda");
                        }
                        if (inquilinoId == null) {
                                throw new IllegalArgumentException(
                                                "Debe indicarse el inquilino de la experiencia");
                        }
                        inquilino = usuarioRepository.findById(inquilinoId)
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "No se ha encontrado al inquilino"));
                } else {
                        throw new IllegalArgumentException(
                                        "El rol no permite iniciar una experiencia de alquiler");
                }

                if (inquilino.getRol() != Rol.INQUILINO) {
                        throw new IllegalArgumentException(
                                        "El participante debe tener el rol INQUILINO");
                }

                if (inquilino.getId().equals(casero.getId())) {
                        throw new IllegalArgumentException(
                                        "El inquilino y el casero deben ser personas distintas");
                }

                ExperienciaAlquiler experiencia = new ExperienciaAlquiler();
                experiencia.setVivienda(vivienda);
                experiencia.setInquilino(inquilino);
                experiencia.setCasero(casero);
                experiencia.setFechaInicio(fechaInicio);
                experiencia.setFechaFin(fechaFin);
                experiencia.setEstado(
                                ExperienciaAlquiler.EstadoExperiencia.PENDIENTE_CONFIRMACION);
                experiencia.setConfirmacionInquilino(iniciador.getId().equals(inquilino.getId()));
                experiencia.setConfirmacionCasero(iniciador.getId().equals(casero.getId()));
                experiencia.setFechaRegistro(LocalDateTime.now());

                return experienciaRepository.save(experiencia);
        }

        private void validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
                if (fechaInicio == null) {
                        throw new IllegalArgumentException(
                                        "La fecha de inicio es obligatoria");
                }

                if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
                        throw new IllegalArgumentException(
                                        "La fecha de fin no puede ser anterior a la fecha de inicio");
                }
        }

        @Transactional
        public ExperienciaAlquiler confirmarExperiencia(
                        String emailUsuario,
                        Long experienciaId) {

                Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No se ha encontrado al usuario autenticado"));

                ExperienciaAlquiler experiencia = experienciaRepository
                                .findById(experienciaId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No se ha encontrado la experiencia"));

                if (experiencia.getEstado() != ExperienciaAlquiler.EstadoExperiencia.PENDIENTE_CONFIRMACION) {
                        throw new IllegalArgumentException(
                                        "La experiencia no admite más confirmaciones");
                }

                if (usuario.getId().equals(experiencia.getInquilino().getId())) {
                        if (experiencia.isConfirmacionInquilino()) {
                                throw new IllegalArgumentException(
                                                "El inquilino ya ha confirmado su participación");
                        }

                        experiencia.setConfirmacionInquilino(true);

                } else if (usuario.getId().equals(experiencia.getCasero().getId())) {
                        if (experiencia.isConfirmacionCasero()) {
                                throw new IllegalArgumentException(
                                                "El casero ya ha confirmado su participación");
                        }

                        experiencia.setConfirmacionCasero(true);

                } else {
                        throw new IllegalArgumentException(
                                        "No participas en esta experiencia de alquiler");
                }

                if (experiencia.isConfirmacionInquilino()
                                && experiencia.isConfirmacionCasero()) {
                        experiencia.setEstado(
                                        ExperienciaAlquiler.EstadoExperiencia.CONFIRMADA);
                }

                return experienciaRepository.save(experiencia);
        }

        @Transactional(readOnly = true)
        public List<ExperienciaAlquilerPendienteResponse> obtenerPendientesCasero(
                        String emailCasero) {

                Usuario casero = usuarioRepository.findByEmail(emailCasero)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No se ha encontrado al usuario autenticado"));

                if (casero.getRol() != Rol.CASERO) {
                        throw new IllegalArgumentException(
                                        "Solo un casero puede consultar estas experiencias");
                }

                return experienciaRepository
                                .findAllByCaseroIdAndEstadoAndConfirmacionCaseroFalse(
                                                casero.getId(),
                                                ExperienciaAlquiler.EstadoExperiencia.PENDIENTE_CONFIRMACION)
                                .stream()
                                .map(ExperienciaAlquilerPendienteResponse::desde)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<ExperienciaAlquilerListadoResponse> obtenerMisExperiencias(
                        String emailUsuario) {

                Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No se ha encontrado al usuario autenticado"));

                List<ExperienciaAlquiler> experiencias;

                if (usuario.getRol() == Rol.INQUILINO) {
                        experiencias = experienciaRepository
                                        .findAllByInquilinoId(usuario.getId());

                } else if (usuario.getRol() == Rol.CASERO) {
                        experiencias = experienciaRepository
                                        .findAllByCaseroId(usuario.getId());

                } else {
                        throw new IllegalArgumentException(
                                        "El rol no permite consultar estas experiencias");
                }

                return experiencias.stream()
                                .map(ExperienciaAlquilerListadoResponse::desde)
                                .toList();
        }

        @Scheduled(cron = "0 0 * * * *")
        @Transactional
        public void finalizarExperienciasVencidas() {
                LocalDate hoy = LocalDate.now();

                List<ExperienciaAlquiler> experienciasConfirmadas = experienciaRepository.findAllByEstado(
                                ExperienciaAlquiler.EstadoExperiencia.CONFIRMADA);

                experienciasConfirmadas.stream()
                                .filter(experiencia -> experiencia.getFechaFin() != null)
                                .filter(experiencia -> !experiencia.getFechaFin().isAfter(hoy))
                                .forEach(experiencia -> {
                                        experiencia.setEstado(
                                                        ExperienciaAlquiler.EstadoExperiencia.FINALIZADA);
                                        experienciaRepository.save(experiencia);
                                });
        }
}