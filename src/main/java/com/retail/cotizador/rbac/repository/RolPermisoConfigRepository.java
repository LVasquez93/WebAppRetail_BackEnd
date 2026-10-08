package com.retail.cotizador.rbac.repository;

import com.retail.cotizador.rbac.entity.RolPermisoConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolPermisoConfigRepository extends JpaRepository<RolPermisoConfig, Long> {
    Optional<RolPermisoConfig> findByRol(String rol);
}
