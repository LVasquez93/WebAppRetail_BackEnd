package com.retail.cotizador.equipos.controller;

import com.retail.cotizador.equipos.dto.EquipoDto;
import com.retail.cotizador.equipos.service.EquipoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/equipos")
@RequiredArgsConstructor
public class EquipoController {

    private final EquipoService equipoService;

    @GetMapping
    public ResponseEntity<List<EquipoDto>> listarOBuscar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long sucursalId) {
        return ResponseEntity.ok(equipoService.listarOBuscar(q, sucursalId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipoDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(equipoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<EquipoDto> crear(@Valid @RequestBody EquipoDto dto) {
        EquipoDto creado = equipoService.crearOActualizar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipoDto> actualizar(@PathVariable Long id, @Valid @RequestBody EquipoDto dto) {
        return ResponseEntity.ok(equipoService.actualizar(id, dto));
    }

    @PostMapping("/lote")
    public ResponseEntity<List<EquipoDto>> crearLote(@RequestBody List<EquipoDto> dtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(equipoService.crearLote(dtos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        equipoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
