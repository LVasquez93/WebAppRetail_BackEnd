package com.retail.cotizador.rbac.controller;

import com.retail.cotizador.auth.security.UserPrincipal;
import com.retail.cotizador.rbac.dto.PermisoDefinicionDto;
import com.retail.cotizador.rbac.dto.RbacMatrizDto;
import com.retail.cotizador.rbac.dto.UsuarioPermisosDto;
import com.retail.cotizador.rbac.service.RbacService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rbac")
@RequiredArgsConstructor
public class RbacController {

    private final RbacService rbacService;

    @GetMapping("/catalogo")
    public ResponseEntity<List<PermisoDefinicionDto>> obtenerCatalogoPermisos() {
        return ResponseEntity.ok(rbacService.obtenerCatalogoPermisos());
    }

    @GetMapping("/matriz")
    public ResponseEntity<RbacMatrizDto> obtenerMatriz() {
        return ResponseEntity.ok(rbacService.obtenerMatriz());
    }

    @PutMapping("/matriz")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RbacMatrizDto> guardarMatriz(@RequestBody RbacMatrizDto matrizDto) {
        return ResponseEntity.ok(rbacService.guardarMatriz(matrizDto));
    }

    @PostMapping("/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RbacMatrizDto> restablecerDefaults() {
        return ResponseEntity.ok(rbacService.restablecerDefaults());
    }

    @GetMapping("/usuarios/{id}/permisos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE_GENERAL') or hasRole('GERENTE_SUCURSAL') or hasRole('GERENTE')")
    public ResponseEntity<UsuarioPermisosDto> obtenerPermisosUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(rbacService.obtenerPermisosUsuario(id));
    }

    @PutMapping("/usuarios/{id}/permisos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE_GENERAL') or hasRole('GERENTE_SUCURSAL') or hasRole('GERENTE')")
    public ResponseEntity<UsuarioPermisosDto> guardarPermisosUsuario(
            @PathVariable Long id,
            @RequestBody List<String> permisos) {
        return ResponseEntity.ok(rbacService.guardarPermisosUsuario(id, permisos));
    }

    @PostMapping("/usuarios/{id}/permisos/reset")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE_GENERAL') or hasRole('GERENTE_SUCURSAL') or hasRole('GERENTE')")
    public ResponseEntity<UsuarioPermisosDto> restablecerPermisosUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(rbacService.restablecerPermisosUsuario(id));
    }

    @GetMapping("/mis-permisos")
    public ResponseEntity<List<String>> obtenerMisPermisos(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(rbacService.obtenerPermisosEfectivosUsuarioAutenticado(userPrincipal));
    }
}
