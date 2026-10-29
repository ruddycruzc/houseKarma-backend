package com.housekarma.housekarma.valoracion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


@Entity
@Table(
    name = "valoracion_criterio",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_valoracion_criterio",
            columnNames = {"valoracion_id", "criterio"}
        )
    }
)

public class ValoracionCriterio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "valoracion_id", nullable = false)
    private Valoracion valoracion;

    @Column(name = "criterio", nullable = false, length = 50)
    private String criterio;

    @Column(name = "puntuacion", nullable = false)
    private Integer puntuacion;

    protected ValoracionCriterio() {
        // Constructor requerido por JPA.
    }

    public ValoracionCriterio(
        Valoracion valoracion,
        String criterio,
        Integer puntuacion
    ) {
        this.valoracion = valoracion;
        this.criterio = criterio;
        this.puntuacion = puntuacion;
    }

    public Long getId() {
        return id;
    }

    public Valoracion getValoracion() {
        return valoracion;
    }

    public String getCriterio() {
        return criterio;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }
}
