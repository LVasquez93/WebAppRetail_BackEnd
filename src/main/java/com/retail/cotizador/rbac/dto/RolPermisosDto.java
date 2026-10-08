package com.retail.cotizador.rbac.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolPermisosDto {
    private String rol;
    private String nombreRol;
    private String descripcion;
    private List<String> permisos;
}
