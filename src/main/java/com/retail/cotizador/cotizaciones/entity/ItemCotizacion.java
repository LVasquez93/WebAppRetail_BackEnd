package com.retail.cotizador.cotizaciones.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "cotizacion_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ItemCotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer itemNumero;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcionEquipo;

    @Column(length = 100)
    private String partNumber; // PN: ...

    @Column(columnDefinition = "TEXT")
    private String caracteristicas; // Especificaciones o características técnicas por cada equipo

    @Column(nullable = false, length = 80)
    private String tiempoEntrega;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalLinea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cotizacion_id", nullable = false)
    private Cotizacion cotizacion;

    /**
     * Retorna la lista de características técnicas limpias para iterar en la plantilla PDF
     */
    public List<String> getListaCaracteristicas() {
        if (caracteristicas == null || caracteristicas.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(caracteristicas.split("\\r?\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.startsWith("•") || s.startsWith("-") || s.startsWith("*") ? s.substring(1).trim() : s)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
