package com.retail.cotizador.sucursales.controller;

import com.retail.cotizador.auth.security.UserPrincipal;
import com.retail.cotizador.sucursales.dto.SucursalDto;
import com.retail.cotizador.sucursales.service.SucursalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sucursales")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    @GetMapping
    public ResponseEntity<List<SucursalDto>> listar(
            @RequestParam(required = false) Long empresaId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            empresaId = userPrincipal.getEmpresaId();
        }
        return ResponseEntity.ok(sucursalService.listarActivas(empresaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(sucursalService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE_GENERAL')")
    public ResponseEntity<SucursalDto> crear(
            @Valid @RequestBody SucursalDto dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            dto.setEmpresaId(userPrincipal.getEmpresaId());
        }
        return new ResponseEntity<>(sucursalService.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE_GENERAL') or hasRole('GERENTE_SUCURSAL') or hasRole('GERENTE')")
    public ResponseEntity<SucursalDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SucursalDto dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null && "ROLE_GERENTE_SUCURSAL".equals(userPrincipal.getRol())) {
            if (userPrincipal.getSucursalId() != null && !userPrincipal.getSucursalId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        return ResponseEntity.ok(sucursalService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE_GENERAL')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        sucursalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
