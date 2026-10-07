package com.retail.cotizador.cotizaciones.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CotizacionRequestDto {

    private String codigoCotizacion;
    private Long empresaId;
    private Long sucursalId;

    @NotBlank
    private String usuarioEmisor;

    @NotNull
    private LocalDate fechaEmision;

    @NotBlank
    private String contactoCliente;

    @NotBlank
    private String razonSocialCliente;

    private String nombreComercial;

    @NotBlank
    private String formaPago;

    private String notaImportante;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal subtotalSinIva;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal montoIva;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal totalInversion;

    private String totalEnLetras;

    @NotEmpty
    @Valid
    private List<ItemDto> items;
}
