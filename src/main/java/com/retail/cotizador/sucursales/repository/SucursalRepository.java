package com.retail.cotizador.sucursales.repository;

import com.retail.cotizador.sucursales.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    List<Sucursal> findAllByActivoTrueOrderByIdAsc();
    Optional<Sucursal> findByCodigo(String codigo);
}
