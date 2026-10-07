package com.retail.cotizador.cotizaciones.repository;

import com.retail.cotizador.cotizaciones.entity.Cotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {
    Optional<Cotizacion> findByCodigoCotizacion(String codigoCotizacion);
    long countByCodigoCotizacionStartingWith(String prefix);

    org.springframework.data.domain.Page<Cotizacion> findAllBySucursalId(Long sucursalId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT c FROM Cotizacion c WHERE " +
           "(:empresaId IS NULL OR c.empresaId = :empresaId) AND " +
           "(:sucursalId IS NULL OR c.sucursalId = :sucursalId) " +
           "ORDER BY c.fechaCreacion DESC")
    org.springframework.data.domain.Page<Cotizacion> filtrarCotizaciones(
            @Param("empresaId") Long empresaId,
            @Param("sucursalId") Long sucursalId,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT DISTINCT c FROM Cotizacion c LEFT JOIN FETCH c.items WHERE c.id = :id")
    Optional<Cotizacion> findByIdWithItems(@Param("id") Long id);
}
