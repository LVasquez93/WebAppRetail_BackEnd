package com.retail.cotizador.rbac.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rbac_rol_permisos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RolPermisoConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String rol;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String permisosJson;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
