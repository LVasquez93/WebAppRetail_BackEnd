package com.retail.cotizador.cotizaciones.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CotizacionResponseDto {
    private Long id;
    private String codigoCotizacion;
    private Long sucursalId;
    private String nombreSucursal;
    private String usuarioEmisor;
    private LocalDate fechaEmision;
    private String contactoCliente;
    private String razonSocialCliente;
    private String nombreComercial;
    private String formaPago;
    private String notaImportante;
    private BigDecimal subtotalSinIva;
    private BigDecimal montoIva;
    private BigDecimal totalInversion;
    private String totalEnLetras;
    private List<ItemDto> items;
    private LocalDateTime fechaCreacion;
}
