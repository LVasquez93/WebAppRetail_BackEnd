package com.retail.cotizador.sucursales.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sucursales")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empresa_id")
    private Long empresaId;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String razonSocial;

    @Column(length = 200)
    private String nombreComercial;

    @Column(length = 250)
    private String direccion;

    @Column(length = 50)
    private String telefono;

    @Column(length = 100)
    private String correo;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String prefijoCotizacion = "COT";

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String headerBannerBase64;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String footerBannerBase64;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String firmaBase64;

    @Column(length = 150)
    @Builder.Default
    private String nombreFirmante = "ING. ERICK RAMIREZ";

    @Column(length = 150)
    @Builder.Default
    private String cargoFirmante = "GERENTE GENERAL";

    @Column(length = 250)
    private String formaPagoPredeterminada;

    @Column(columnDefinition = "TEXT")
    private String notaPredeterminada;

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
        if (this.prefijoCotizacion == null || this.prefijoCotizacion.isBlank()) {
            this.prefijoCotizacion = "COT";
        }
    }
}
