
package com.housekarma.housekarma.vivienda.entity;

import jakarta.persistence.*;
import com.housekarma.housekarma.usuario.entity.Usuario;

import java.time.LocalDateTime;

@Entity
@Table(name = "vivienda", uniqueConstraints = {
        @UniqueConstraint(name = "uk_vivienda_direccion_normalizada", columnNames = "direccion_normalizada")
}, indexes = {
        @Index(name = "idx_vivienda_localidad", columnList = "localidad"),
        @Index(name = "idx_vivienda_codigo_postal", columnList = "codigo_postal")
})
public class Vivienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "casero_id", nullable = false)
    private Usuario casero;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String numero;

    private String complemento;

    @Column(nullable = false)
    private String localidad;

    @Column(name = "codigo_postal", nullable = false)
    private String codigoPostal;

    @Column(nullable = false)
    private String provincia;

    @Column(name = "direccion_normalizada", nullable = false, unique = true, length = 500)
    private String direccionNormalizada;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    public Vivienda() {
    }

    public Long getId() {
        return id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDireccionNormalizada() {
        return direccionNormalizada;
    }

    public void setDireccionNormalizada(String direccionNormalizada) {
        this.direccionNormalizada = direccionNormalizada;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Usuario getCasero() {
        return casero;
    }

    public void setCasero(Usuario casero) {
        this.casero = casero;
    }
}
