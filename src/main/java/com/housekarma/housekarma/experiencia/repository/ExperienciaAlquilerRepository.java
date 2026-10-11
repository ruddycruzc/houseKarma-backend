package com.housekarma.housekarma.experiencia.repository;

import com.housekarma.housekarma.experiencia.entity.ExperienciaAlquiler;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienciaAlquilerRepository
        extends JpaRepository<ExperienciaAlquiler, Long> {

    List<ExperienciaAlquiler> findAllByViviendaId(Long viviendaId);

    List<ExperienciaAlquiler> findAllByInquilinoId(Long inquilinoId);

    List<ExperienciaAlquiler> findAllByCaseroId(Long caseroId);

    List<ExperienciaAlquiler> findAllByEstado(
            ExperienciaAlquiler.EstadoExperiencia estado
    );
}