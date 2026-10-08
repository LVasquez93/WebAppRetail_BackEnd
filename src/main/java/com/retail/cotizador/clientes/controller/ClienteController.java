package com.retail.cotizador.clientes.controller;

import com.retail.cotizador.auth.security.UserPrincipal;
import com.retail.cotizador.clientes.dto.ClienteDto;
import com.retail.cotizador.clientes.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteDto>> listarOBuscar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Long sucursalId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        // Segregación multi-tenant: Si no es ADMIN del SaaS, restringir estrictamente a su empresa
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            empresaId = userPrincipal.getEmpresaId();
        }

        return ResponseEntity.ok(clienteService.listarOBuscar(q, empresaId, sucursalId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDto> obtenerPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ClienteDto dto = clienteService.obtenerPorId(id);
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            if (dto.getEmpresaId() != null && !userPrincipal.getEmpresaId().equals(dto.getEmpresaId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<ClienteDto> crear(
            @Valid @RequestBody ClienteDto dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            dto.setEmpresaId(userPrincipal.getEmpresaId());
        }
        ClienteDto creado = clienteService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteDto dto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            ClienteDto existente = clienteService.obtenerPorId(id);
            if (existente.getEmpresaId() != null && !userPrincipal.getEmpresaId().equals(existente.getEmpresaId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            dto.setEmpresaId(userPrincipal.getEmpresaId());
        }
        return ResponseEntity.ok(clienteService.actualizar(id, dto));
    }

    @PostMapping("/lote")
    public ResponseEntity<List<ClienteDto>> crearLote(
            @RequestBody List<ClienteDto> dtos,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            for (ClienteDto d : dtos) {
                d.setEmpresaId(userPrincipal.getEmpresaId());
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crearLote(dtos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal != null && !"ROLE_ADMIN".equals(userPrincipal.getRol())) {
            ClienteDto existente = clienteService.obtenerPorId(id);
            if (existente.getEmpresaId() != null && !userPrincipal.getEmpresaId().equals(existente.getEmpresaId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
