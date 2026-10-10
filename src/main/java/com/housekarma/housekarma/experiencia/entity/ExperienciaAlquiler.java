
package com.housekarma.housekarma.experiencia.entity;

import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.vivienda.entity.Vivienda;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "experiencia_alquiler", indexes = {
        @Index(name = "idx_experiencia_vivienda", columnList = "vivienda_id"),
        @Index(name = "idx_experiencia_inquilino", columnList = "inquilino_id"),
        @Index(name = "idx_experiencia_casero", columnList = "casero_id"),
        @Index(name = "idx_experiencia_estado", columnList = "estado")
})
public class ExperienciaAlquiler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vivienda_id", nullable = false)
    private Vivienda vivienda;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inquilino_id", nullable = false)
    private Usuario inquilino;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "casero_id", nullable = false)
    private Usuario casero;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoExperiencia estado = EstadoExperiencia.PENDIENTE_CONFIRMACION;

    @Column(name = "confirmacion_inquilino", nullable = false)
    private boolean confirmacionInquilino;

    @Column(name = "confirmacion_casero", nullable = false)
    private boolean confirmacionCasero;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    public ExperienciaAlquiler() {
    }

    public Long getId() {
        return id;
    }

    public Vivienda getVivienda() {
        return vivienda;
    }

    public void setVivienda(Vivienda vivienda) {
        this.vivienda = vivienda;
    }

    public Usuario getInquilino() {
        return inquilino;
    }

    public void setInquilino(Usuario inquilino) {
        this.inquilino = inquilino;
    }

    public Usuario getCasero() {
        return casero;
    }

    public void setCasero(Usuario casero) {
        this.casero = casero;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public EstadoExperiencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoExperiencia estado) {
        this.estado = estado;
    }

    public boolean isConfirmacionInquilino() {
        return confirmacionInquilino;
    }

    public void setConfirmacionInquilino(boolean confirmacionInquilino) {
        this.confirmacionInquilino = confirmacionInquilino;
    }

    public boolean isConfirmacionCasero() {
        return confirmacionCasero;
    }

    public void setConfirmacionCasero(boolean confirmacionCasero) {
        this.confirmacionCasero = confirmacionCasero;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public enum EstadoExperiencia {
        PENDIENTE_CONFIRMACION,
        CONFIRMADA,
        FINALIZADA,
        CANCELADA
    }
}
