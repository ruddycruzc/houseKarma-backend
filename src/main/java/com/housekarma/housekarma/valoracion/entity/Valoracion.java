package com.housekarma.housekarma.valoracion.entity;

import com.housekarma.housekarma.experiencia.entity.ExperienciaAlquiler;
import com.housekarma.housekarma.usuario.entity.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "valoracion",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_valoracion_experiencia_autor",
        columnNames = {"experiencia_id", "autor_id"}
    )
)
public class Valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "experiencia_id", nullable = false)
    private ExperienciaAlquiler experiencia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Usuario destinatario;

    @Column(nullable = false)
    private Integer puntuacion;

    @Column(length = 1000)
    private String comentario;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    void antesDeGuardar() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public ExperienciaAlquiler getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(ExperienciaAlquiler experiencia) {
        this.experiencia = experiencia;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public Usuario getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Usuario destinatario) {
        this.destinatario = destinatario;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
}