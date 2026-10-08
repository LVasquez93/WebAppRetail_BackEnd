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
    public ResponseEntity<UsuarioDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PostMapping
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
    public ResponseEntity<UsuarioDto> actualizar(
            @PathVariable Long id,
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
        return ResponseEntity.ok(usuarioService.actualizar(id, dto));
    }

    @PostMapping("/lote")
    public ResponseEntity<List<UsuarioDto>> crearLote(@RequestBody List<UsuarioDto> dtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearLote(dtos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
