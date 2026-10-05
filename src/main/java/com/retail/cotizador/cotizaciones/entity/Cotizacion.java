package com.retail.cotizador.cotizaciones.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cotizaciones")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigoCotizacion;

    @Column(name = "sucursal_id")
    private Long sucursalId;

    @Column(nullable = false, length = 50)
    private String usuarioEmisor;

    @Column(nullable = false)
    private LocalDate fechaEmision;

    @Column(nullable = false, length = 150)
    private String contactoCliente;

    @Column(nullable = false, length = 200)
    private String razonSocialCliente;

    @Column(length = 200)
    private String nombreComercial;

    @Column(nullable = false, length = 250)
    private String formaPago;

    @Column(columnDefinition = "TEXT")
    private String notaImportante;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalSinIva;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montoIva;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalInversion;

    @Column(nullable = false, length = 255)
    private String totalEnLetras;

    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<ItemCotizacion> items = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public void addItem(ItemCotizacion item) {
        items.add(item);
        item.setCotizacion(this);
    }
}
