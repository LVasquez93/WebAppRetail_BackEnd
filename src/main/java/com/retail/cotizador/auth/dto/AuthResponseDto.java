package com.retail.cotizador.auth.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {
    private String token;
    private String tokenType;
    private Long expiresIn;
    private Long id;
    private String username;
    private String nombreCompleto;
    private String correo;
    private String cargo;
    private String rol;
    private Long empresaId;
    private String empresaNombre;
    private Long sucursalId;
    private String sucursalCodigo;
    private String sucursalNombre;
}
