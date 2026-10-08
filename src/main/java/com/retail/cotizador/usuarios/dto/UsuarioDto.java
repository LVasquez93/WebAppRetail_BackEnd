package com.retail.cotizador.usuarios.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDto {
    private Long id;

    @NotBlank(message = "El código de usuario es obligatorio")
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    private String correo;
    private String cargo;
    private String rol;
    private Long empresaId;
    private Long sucursalId;
    private List<String> permisosPersonalizados;
    private Boolean tienePermisosPersonalizados;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}
