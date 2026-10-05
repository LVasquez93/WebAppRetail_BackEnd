package com.retail.cotizador.clientes.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDto {
    private Long id;

    @NotBlank(message = "La razón social es obligatoria")
    private String razonSocial;

    private String nombreComercial;
    private String contactoPrincipal;
    private String telefono;
    private String correo;
    private String direccion;
    private Long sucursalId;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}
