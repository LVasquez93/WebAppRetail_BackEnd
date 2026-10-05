package com.retail.cotizador.clientes.controller;

import com.retail.cotizador.clientes.dto.ClienteDto;
import com.retail.cotizador.clientes.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @RequestParam(required = false) Long sucursalId) {
        return ResponseEntity.ok(clienteService.listarOBuscar(q, sucursalId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ClienteDto> crear(@Valid @RequestBody ClienteDto dto) {
        ClienteDto creado = clienteService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDto> actualizar(@PathVariable Long id, @Valid @RequestBody ClienteDto dto) {
        return ResponseEntity.ok(clienteService.actualizar(id, dto));
    }

    @PostMapping("/lote")
    public ResponseEntity<List<ClienteDto>> crearLote(@RequestBody List<ClienteDto> dtos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crearLote(dtos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
