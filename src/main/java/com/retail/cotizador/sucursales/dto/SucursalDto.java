package com.retail.cotizador.sucursales.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SucursalDto {
    private Long id;
    private Long empresaId;

    @NotBlank(message = "El código de la sucursal es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre de la sucursal es obligatorio")
    private String nombre;

    @NotBlank(message = "La razón social es obligatoria")
    private String razonSocial;

    private String nombreComercial;
    private String direccion;
    private String telefono;
    private String correo;
    private String prefijoCotizacion;
    private String headerBannerBase64;
    private String footerBannerBase64;
    private String firmaBase64;
    private String nombreFirmante;
    private String cargoFirmante;
    private String formaPagoPredeterminada;
    private String notaPredeterminada;

    // Opciones de configuración comercial y fiscal
    private BigDecimal porcentajeIva;
    private String monedaCodigo;
    private String monedaSimbolo;
    private String monedaNombre;
    private Integer diasValidezCotizacion;
    private String tiempoEntregaPredeterminado;
    private String garantiaPredeterminada;
    private Boolean mostrarIvaDesglosado;

    private Boolean activo;
}
