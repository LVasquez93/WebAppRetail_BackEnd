package com.retail.cotizador.sucursales.controller;

import com.retail.cotizador.sucursales.dto.SucursalDto;
import com.retail.cotizador.sucursales.service.SucursalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sucursales")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    @GetMapping
    public ResponseEntity<List<SucursalDto>> listar() {
        return ResponseEntity.ok(sucursalService.listarActivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(sucursalService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<SucursalDto> crear(@Valid @RequestBody SucursalDto dto) {
        return new ResponseEntity<>(sucursalService.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SucursalDto> actualizar(@PathVariable Long id, @Valid @RequestBody SucursalDto dto) {
        return ResponseEntity.ok(sucursalService.actualizar(id, dto));
    }
}
