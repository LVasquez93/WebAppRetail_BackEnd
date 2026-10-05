package com.retail.cotizador.equipos.repository;

import com.retail.cotizador.equipos.entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    List<Equipo> findByActivoTrueOrderByDescripcionAsc();

    @Query("SELECT e FROM Equipo e WHERE e.activo = true AND (:sucursalId IS NULL OR e.sucursalId = :sucursalId OR e.sucursalId IS NULL) ORDER BY e.descripcion ASC")
    List<Equipo> listarPorSucursal(@Param("sucursalId") Long sucursalId);

    Optional<Equipo> findByPartNumberIgnoreCase(String partNumber);

    Optional<Equipo> findByDescripcionIgnoreCase(String descripcion);

    @Query("SELECT e FROM Equipo e WHERE e.activo = true AND " +
           "(:sucursalId IS NULL OR e.sucursalId = :sucursalId OR e.sucursalId IS NULL) AND (" +
           "LOWER(e.descripcion) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "(e.partNumber IS NOT NULL AND LOWER(e.partNumber) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           "(e.caracteristicas IS NOT NULL AND LOWER(e.caracteristicas) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           "(e.categoria IS NOT NULL AND LOWER(e.categoria) LIKE LOWER(CONCAT('%', :query, '%')))) " +
           "ORDER BY e.descripcion ASC")
    List<Equipo> buscarEquipos(@Param("query") String query, @Param("sucursalId") Long sucursalId);
}
