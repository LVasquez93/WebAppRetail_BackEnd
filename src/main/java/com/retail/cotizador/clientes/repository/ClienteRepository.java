package com.retail.cotizador.clientes.repository;

import com.retail.cotizador.clientes.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByActivoTrueOrderByRazonSocialAsc();

    @Query("SELECT c FROM Cliente c WHERE c.activo = true AND (:sucursalId IS NULL OR c.sucursalId = :sucursalId OR c.sucursalId IS NULL) ORDER BY c.razonSocial ASC")
    List<Cliente> listarPorSucursal(@Param("sucursalId") Long sucursalId);

    Optional<Cliente> findByRazonSocialIgnoreCase(String razonSocial);

    @Query("SELECT c FROM Cliente c WHERE c.activo = true AND " +
           "(:sucursalId IS NULL OR c.sucursalId = :sucursalId OR c.sucursalId IS NULL) AND (" +
           "LOWER(c.razonSocial) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.nombreComercial) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.contactoPrincipal) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Cliente> buscarClientes(@Param("query") String query, @Param("sucursalId") Long sucursalId);
}
