package com.retail.cotizador.sucursales.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
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

    @Column(name = "porcentaje_iva", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal porcentajeIva = new BigDecimal("13.00");

    @Column(name = "moneda_codigo", length = 10)
    @Builder.Default
    private String monedaCodigo = "USD";

    @Column(name = "moneda_simbolo", length = 10)
    @Builder.Default
    private String monedaSimbolo = "$";

    @Column(name = "moneda_nombre", length = 50)
    @Builder.Default
    private String monedaNombre = "DOLARES";

    @Column(name = "dias_validez_cotizacion")
    @Builder.Default
    private Integer diasValidezCotizacion = 15;

    @Column(name = "tiempo_entrega_predeterminado", length = 100)
    @Builder.Default
    private String tiempoEntregaPredeterminado = "De 5 a 6 semanas";

    @Column(name = "garantia_predeterminada", length = 200)
    @Builder.Default
    private String garantiaPredeterminada = "1 año contra defectos de fábrica";

    @Column(name = "mostrar_iva_desglosado")
    @Builder.Default
    private Boolean mostrarIvaDesglosado = true;

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
        if (this.porcentajeIva == null) {
            this.porcentajeIva = new BigDecimal("13.00");
        }
        if (this.monedaCodigo == null || this.monedaCodigo.isBlank()) {
            this.monedaCodigo = "USD";
        }
        if (this.monedaSimbolo == null || this.monedaSimbolo.isBlank()) {
            this.monedaSimbolo = "$";
        }
        if (this.monedaNombre == null || this.monedaNombre.isBlank()) {
            this.monedaNombre = "DOLARES";
        }
        if (this.diasValidezCotizacion == null) {
            this.diasValidezCotizacion = 15;
        }
        if (this.tiempoEntregaPredeterminado == null || this.tiempoEntregaPredeterminado.isBlank()) {
            this.tiempoEntregaPredeterminado = "De 5 a 6 semanas";
        }
        if (this.garantiaPredeterminada == null || this.garantiaPredeterminada.isBlank()) {
            this.garantiaPredeterminada = "1 año contra defectos de fábrica";
        }
        if (this.mostrarIvaDesglosado == null) {
            this.mostrarIvaDesglosado = true;
        }
    }
}
