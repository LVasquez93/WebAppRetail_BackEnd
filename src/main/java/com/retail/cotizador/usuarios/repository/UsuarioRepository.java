package com.retail.cotizador.usuarios.repository;

import com.retail.cotizador.usuarios.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByActivoTrueOrderByNombreCompletoAsc();

    List<Usuario> findByEmpresaIdAndActivoTrueOrderByNombreCompletoAsc(Long empresaId);

    List<Usuario> findByEmpresaId(Long empresaId);

    Optional<Usuario> findByUsername(String username);

    List<Usuario> findByRolAndActivoTrueOrderByNombreCompletoAsc(String rol);

    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND (" +
           "LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.cargo) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Usuario> buscarUsuarios(@Param("query") String query);

    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND " +
           "(:rol IS NULL OR u.rol = :rol) AND " +
           "(:empresaId IS NULL OR u.empresaId = :empresaId) AND (" +
           ":query IS NULL OR :query = '' OR " +
           "LOWER(u.nombreCompleto) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(u.cargo) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY u.nombreCompleto ASC")
    Page<Usuario> buscarUsuariosFiltrados(
            @Param("query") String query,
            @Param("empresaId") Long empresaId,
            @Param("rol") String rol,
            Pageable pageable);
}
