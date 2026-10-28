package com.housekarma.housekarma.valoracion.repository;

import com.housekarma.housekarma.valoracion.entity.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    boolean existsByExperienciaIdAndAutorId(
        Long experienciaId,
        Long autorId
    );

    List<Valoracion> findAllByDestinatarioIdOrderByFechaRegistroDesc(
        Long destinatarioId
    );

    @Query("""
        SELECT AVG(v.puntuacion)
        FROM Valoracion v
        WHERE v.destinatario.id = :destinatarioId
        """)
    Double calcularMediaRecibida(
        @Param("destinatarioId") Long destinatarioId
    );

    long countByDestinatarioId(Long destinatarioId);
}