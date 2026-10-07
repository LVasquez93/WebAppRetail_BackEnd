package com.retail.cotizador.empresas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpresaDto {
    private Long id;

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    private String nombre;

    private String razonSocial;
    private String nit;
    private String telefono;
    private String correo;
    private String direccion;
    private String logoBase64;
    private Boolean activo;
    private LocalDateTime fechaCreacion;

    // Campos para el Gerente de la empresa
    private Long gerenteId;
    private String gerenteUsername;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String gerentePassword;
    private String gerenteNombreCompleto;
    private String gerenteCorreo;
}
