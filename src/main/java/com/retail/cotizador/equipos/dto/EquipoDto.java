package com.retail.cotizador.equipos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipoDto {
    private Long id;

    @NotBlank(message = "El nombre o descripción del equipo es obligatorio")
    private String descripcion;

    private String partNumber;
    private String caracteristicas;
    private BigDecimal precioReferencial;
    private String tiempoEntregaPredeterminado;
    private String categoria;
    private Long empresaId;
    private Long sucursalId;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}
