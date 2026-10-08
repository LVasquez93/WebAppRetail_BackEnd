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
public class UsuarioPermisosDto {
    private Long usuarioId;
    private String username;
    private String nombreCompleto;
    private String cargo;
    private String rol;
    private Long empresaId;
    private Long sucursalId;
    private List<String> permisosRolPorDefecto;
    private List<String> permisosEfectivos;
    private List<String> permisosEspecialesAsignados;
    private boolean tienePermisosPersonalizados;
}
