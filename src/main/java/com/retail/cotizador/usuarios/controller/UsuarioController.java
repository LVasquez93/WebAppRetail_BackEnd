package com.retail.cotizador.usuarios.controller;

import com.retail.cotizador.auth.security.UserPrincipal;
import com.retail.cotizador.usuarios.dto.UsuarioDto;
import com.retail.cotizador.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDto>> listarOBuscar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Boolean soloAdmins,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            empresaId = userPrincipal.getEmpresaId();
            soloAdmins = false;
        }
        return ResponseEntity.ok(usuarioService.listarOBuscar(q, empresaId, soloAdmins));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> obtenerPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UsuarioDto usuario = usuarioService.obtenerPorId(id);
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            if (usuario.getEmpresaId() != null && !userPrincipal.getEmpresaId().equals(usuario.getEmpresaId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE_GENERAL') or hasRole('GERENTE_SUCURSAL') or hasRole('GERENTE')")
    public ResponseEntity<UsuarioDto> crear(
            @Valid @RequestBody UsuarioDto dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            dto.setEmpresaId(userPrincipal.getEmpresaId());
            if ("ROLE_GERENTE_SUCURSAL".equals(userPrincipal.getRol())) {
                dto.setRol("ROLE_VENTAS");
                dto.setSucursalId(userPrincipal.getSucursalId());
            } else if ("ROLE_GERENTE_GENERAL".equals(userPrincipal.getRol()) || "ROLE_GERENTE".equals(userPrincipal.getRol())) {
                if ("ROLE_ADMIN".equalsIgnoreCase(dto.getRol())) {
                    dto.setRol("ROLE_VENTAS");
                }
            }
        }
        UsuarioDto creado = usuarioService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE_GENERAL') or hasRole('GERENTE_SUCURSAL') or hasRole('GERENTE')")
    public ResponseEntity<UsuarioDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDto dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            UsuarioDto existente = usuarioService.obtenerPorId(id);
            if (existente.getEmpresaId() != null && !userPrincipal.getEmpresaId().equals(existente.getEmpresaId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            dto.setEmpresaId(userPrincipal.getEmpresaId());
            if ("ROLE_GERENTE_SUCURSAL".equals(userPrincipal.getRol())) {
                dto.setRol("ROLE_VENTAS");
                dto.setSucursalId(userPrincipal.getSucursalId());
            } else if ("ROLE_GERENTE_GENERAL".equals(userPrincipal.getRol()) || "ROLE_GERENTE".equals(userPrincipal.getRol())) {
                if ("ROLE_ADMIN".equalsIgnoreCase(dto.getRol())) {
                    dto.setRol("ROLE_VENTAS");
                }
            }
        }
        return ResponseEntity.ok(usuarioService.actualizar(id, dto));
    }

    @PostMapping("/lote")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE_GENERAL') or hasRole('GERENTE_SUCURSAL') or hasRole('GERENTE')")
    public ResponseEntity<List<UsuarioDto>> crearLote(
            @RequestBody List<UsuarioDto> dtos,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            for (UsuarioDto d : dtos) {
                d.setEmpresaId(userPrincipal.getEmpresaId());
                if ("ROLE_GERENTE_SUCURSAL".equals(userPrincipal.getRol())) {
                    d.setRol("ROLE_VENTAS");
                    d.setSucursalId(userPrincipal.getSucursalId());
                } else if (d.getRol() == null || "ROLE_ADMIN".equalsIgnoreCase(d.getRol())) {
                    d.setRol("ROLE_VENTAS");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearLote(dtos));
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE_GENERAL') or hasRole('GERENTE_SUCURSAL') or hasRole('GERENTE')")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            UsuarioDto existente = usuarioService.obtenerPorId(id);
            if (existente.getEmpresaId() != null && !userPrincipal.getEmpresaId().equals(existente.getEmpresaId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
