package com.retail.cotizador.empresas.repository;

import com.retail.cotizador.empresas.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    List<Empresa> findAllByOrderByNombreAsc();
    List<Empresa> findAllByActivoTrueOrderByNombreAsc();
}
