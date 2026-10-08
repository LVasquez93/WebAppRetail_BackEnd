package com.retail.cotizador.rbac.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermisoDefinicionDto {
    private String codigo;
    private String nombre;
    private String descripcion;
    private String categoria;
}
