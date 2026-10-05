package com.retail.cotizador.cotizaciones.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private Long id;

    @NotNull
    @Positive
    private Integer itemNumero;

    @NotBlank
    private String descripcionEquipo;

    private String partNumber;

    private String caracteristicas;

    @NotBlank
    private String tiempoEntrega;

    @NotNull
    @Positive
    private Integer cantidad;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal precioUnitario;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal totalLinea;
}
