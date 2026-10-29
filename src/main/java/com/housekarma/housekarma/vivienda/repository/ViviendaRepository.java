package com.housekarma.housekarma.vivienda.repository;

import com.housekarma.housekarma.vivienda.entity.Vivienda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ViviendaRepository extends JpaRepository<Vivienda, Long> {

    boolean existsByDireccionNormalizada(String direccionNormalizada);

    Optional<Vivienda> findByDireccionNormalizada(String direccionNormalizada);

    List<Vivienda> findAllByCaseroId(Long caseroId);
}