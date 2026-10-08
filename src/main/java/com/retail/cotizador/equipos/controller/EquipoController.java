package com.retail.cotizador.equipos.controller;

import com.retail.cotizador.auth.security.UserPrincipal;
import com.retail.cotizador.common.dto.PageResponseDto;
import com.retail.cotizador.equipos.dto.EquipoDto;
import com.retail.cotizador.equipos.service.EquipoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/equipos")
@RequiredArgsConstructor
public class EquipoController {

    private final EquipoService equipoService;

    @GetMapping
    public ResponseEntity<PageResponseDto<EquipoDto>> listarOBuscar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long sucursalId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(page = 0, size = 15) Pageable pageable) {

        // Segregación multi-tenant: Si no es ADMIN del SaaS, restringir estrictamente a su empresa
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            empresaId = userPrincipal.getEmpresaId();
        }

        Page<EquipoDto> page = equipoService.listarOBuscarPaginado(q, empresaId, sucursalId, pageable);
        return ResponseEntity.ok(PageResponseDto.from(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipoDto> obtenerPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        EquipoDto dto = equipoService.obtenerPorId(id);
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            if (dto.getEmpresaId() != null && !userPrincipal.getEmpresaId().equals(dto.getEmpresaId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<EquipoDto> crear(
            @Valid @RequestBody EquipoDto dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            dto.setEmpresaId(userPrincipal.getEmpresaId());
        }
        EquipoDto creado = equipoService.crearOActualizar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipoDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EquipoDto dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            EquipoDto existente = equipoService.obtenerPorId(id);
            if (existente.getEmpresaId() != null && !userPrincipal.getEmpresaId().equals(existente.getEmpresaId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            dto.setEmpresaId(userPrincipal.getEmpresaId());
        }
        return ResponseEntity.ok(equipoService.actualizar(id, dto));
    }

    @PostMapping("/lote")
    public ResponseEntity<List<EquipoDto>> crearLote(
            @RequestBody List<EquipoDto> dtos,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            for (EquipoDto d : dtos) {
                d.setEmpresaId(userPrincipal.getEmpresaId());
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(equipoService.crearLote(dtos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            EquipoDto existente = equipoService.obtenerPorId(id);
            if (existente.getEmpresaId() != null && !userPrincipal.getEmpresaId().equals(existente.getEmpresaId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        equipoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
