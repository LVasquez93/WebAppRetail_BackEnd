package com.retail.cotizador.usuarios.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(length = 120)
    private String password;

    @Column(nullable = false, length = 150)
    private String nombreCompleto;

    @Column(length = 100)
    private String correo;

    @Column(length = 100)
    private String cargo;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String rol = "ROLE_VENTAS";

    @Column(name = "sucursal_id")
    private Long sucursalId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.activo == null) {
            this.activo = true;
        }
        if (this.rol == null || this.rol.trim().isEmpty()) {
            this.rol = "ROLE_VENTAS";
        }
    }
}
