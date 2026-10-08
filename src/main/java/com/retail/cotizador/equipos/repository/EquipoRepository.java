package com.retail.cotizador.equipos.repository;

import com.retail.cotizador.equipos.entity.Equipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    List<Equipo> findByActivoTrueOrderByDescripcionAsc();

    @Query("SELECT e FROM Equipo e WHERE e.activo = true AND " +
           "(:empresaId IS NULL OR e.empresaId = :empresaId) AND " +
           "(:sucursalId IS NULL OR e.sucursalId = :sucursalId) " +
           "ORDER BY e.descripcion ASC")
    List<Equipo> listarPorEmpresaYSucursal(@Param("empresaId") Long empresaId, @Param("sucursalId") Long sucursalId);

    @Query("SELECT e FROM Equipo e WHERE e.activo = true AND " +
           "(:empresaId IS NULL OR e.empresaId = :empresaId) AND " +
           "(:sucursalId IS NULL OR e.sucursalId = :sucursalId) " +
           "ORDER BY e.descripcion ASC")
    Page<Equipo> listarPorEmpresaYSucursal(@Param("empresaId") Long empresaId, @Param("sucursalId") Long sucursalId, Pageable pageable);

    Optional<Equipo> findByPartNumberIgnoreCase(String partNumber);

    Optional<Equipo> findByDescripcionIgnoreCase(String descripcion);

    @Query("SELECT e FROM Equipo e WHERE e.activo = true AND " +
           "(:empresaId IS NULL OR e.empresaId = :empresaId) AND " +
           "(:sucursalId IS NULL OR e.sucursalId = :sucursalId) AND (" +
           "LOWER(e.descripcion) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "(e.partNumber IS NOT NULL AND LOWER(e.partNumber) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           "(e.caracteristicas IS NOT NULL AND LOWER(e.caracteristicas) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           "(e.categoria IS NOT NULL AND LOWER(e.categoria) LIKE LOWER(CONCAT('%', :query, '%')))) " +
           "ORDER BY e.descripcion ASC")
    List<Equipo> buscarEquipos(@Param("query") String query, @Param("empresaId") Long empresaId, @Param("sucursalId") Long sucursalId);

    @Query("SELECT e FROM Equipo e WHERE e.activo = true AND " +
           "(:empresaId IS NULL OR e.empresaId = :empresaId) AND " +
           "(:sucursalId IS NULL OR e.sucursalId = :sucursalId) AND (" +
           "LOWER(e.descripcion) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "(e.partNumber IS NOT NULL AND LOWER(e.partNumber) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           "(e.caracteristicas IS NOT NULL AND LOWER(e.caracteristicas) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           "(e.categoria IS NOT NULL AND LOWER(e.categoria) LIKE LOWER(CONCAT('%', :query, '%')))) " +
           "ORDER BY e.descripcion ASC")
    Page<Equipo> buscarEquipos(@Param("query") String query, @Param("empresaId") Long empresaId, @Param("sucursalId") Long sucursalId, Pageable pageable);
}
