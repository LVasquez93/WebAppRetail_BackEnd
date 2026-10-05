package com.retail.cotizador.equipos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 100)
    private String partNumber;

    @Column(columnDefinition = "TEXT")
    private String caracteristicas;

    @Column(precision = 12, scale = 2)
    private BigDecimal precioReferencial;

    @Column(length = 80)
    private String tiempoEntregaPredeterminado;

    @Column(length = 100)
    private String categoria;

    @Column(name = "sucursal_id")
    private Long sucursalId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.activo == null) {
            this.activo = true;
        }
    }
}
