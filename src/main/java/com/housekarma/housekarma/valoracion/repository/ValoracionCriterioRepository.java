package com.housekarma.housekarma.valoracion.repository;
import com.housekarma.housekarma.valoracion.entity.ValoracionCriterio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ValoracionCriterioRepository
        extends JpaRepository<ValoracionCriterio, Long> {

    List<ValoracionCriterio> findAllByValoracionId(Long valoracionId);
}