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
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<SucursalDto> crear(@Valid @RequestBody SucursalDto dto) {
        return new ResponseEntity<>(sucursalService.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<SucursalDto> actualizar(@PathVariable Long id, @Valid @RequestBody SucursalDto dto) {
        return ResponseEntity.ok(sucursalService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        sucursalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
