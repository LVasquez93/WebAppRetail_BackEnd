package com.retail.cotizador.equipos.service;

import com.retail.cotizador.common.exception.ResourceNotFoundException;
import com.retail.cotizador.equipos.dto.EquipoDto;
import com.retail.cotizador.equipos.entity.Equipo;
import com.retail.cotizador.equipos.repository.EquipoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipoService {

    private final EquipoRepository equipoRepository;

    @Transactional(readOnly = true)
    public List<EquipoDto> listarOBuscar(String query) {
        return listarOBuscar(query, null);
    }

    @Transactional(readOnly = true)
    public List<EquipoDto> listarOBuscar(String query, Long sucursalId) {
        List<Equipo> list;
        if (query != null && !query.trim().isEmpty()) {
            list = equipoRepository.buscarEquipos(query.trim(), sucursalId);
        } else {
            list = equipoRepository.listarPorSucursal(sucursalId);
        }
        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EquipoDto obtenerPorId(Long id) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo", "id", id));
        return mapToDto(equipo);
    }

    @Transactional
    public EquipoDto crearOActualizar(EquipoDto dto) {
        // Si viene con ID o si ya existe por Part Number o por Descripción exacta, actualizar o retornar
        Optional<Equipo> existente = Optional.empty();
        if (dto.getPartNumber() != null && !dto.getPartNumber().trim().isEmpty()) {
            existente = equipoRepository.findByPartNumberIgnoreCase(dto.getPartNumber().trim());
        }
        if (existente.isEmpty() && dto.getDescripcion() != null && !dto.getDescripcion().trim().isEmpty()) {
            existente = equipoRepository.findByDescripcionIgnoreCase(dto.getDescripcion().trim());
        }

        Equipo equipo;
        if (existente.isPresent()) {
            equipo = existente.get();
            equipo.setDescripcion(dto.getDescripcion().trim());
            if (dto.getPartNumber() != null) {
                equipo.setPartNumber(dto.getPartNumber().trim());
            }
            if (dto.getCaracteristicas() != null) {
                equipo.setCaracteristicas(dto.getCaracteristicas().trim());
            }
            if (dto.getPrecioReferencial() != null) {
                equipo.setPrecioReferencial(dto.getPrecioReferencial());
            }
            if (dto.getTiempoEntregaPredeterminado() != null) {
                equipo.setTiempoEntregaPredeterminado(dto.getTiempoEntregaPredeterminado().trim());
            }
            if (dto.getCategoria() != null) {
                equipo.setCategoria(dto.getCategoria().trim());
            }
            if (dto.getSucursalId() != null) {
                equipo.setSucursalId(dto.getSucursalId());
            }
            equipo.setActivo(true);
        } else {
            equipo = Equipo.builder()
                    .descripcion(dto.getDescripcion().trim())
                    .partNumber(dto.getPartNumber() != null ? dto.getPartNumber().trim() : null)
                    .caracteristicas(dto.getCaracteristicas() != null ? dto.getCaracteristicas().trim() : null)
                    .precioReferencial(dto.getPrecioReferencial())
                    .tiempoEntregaPredeterminado(dto.getTiempoEntregaPredeterminado() != null ? dto.getTiempoEntregaPredeterminado().trim() : "De 5 a 6 semanas")
                    .categoria(dto.getCategoria() != null ? dto.getCategoria().trim() : "General")
                    .sucursalId(dto.getSucursalId())
                    .activo(dto.getActivo() != null ? dto.getActivo() : true)
                    .build();
        }

        return mapToDto(equipoRepository.save(equipo));
    }

    @Transactional
    public EquipoDto actualizar(Long id, EquipoDto dto) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo", "id", id));

        equipo.setDescripcion(dto.getDescripcion().trim());
        equipo.setPartNumber(dto.getPartNumber() != null ? dto.getPartNumber().trim() : null);
        equipo.setCaracteristicas(dto.getCaracteristicas() != null ? dto.getCaracteristicas().trim() : null);
        equipo.setPrecioReferencial(dto.getPrecioReferencial());
        equipo.setTiempoEntregaPredeterminado(dto.getTiempoEntregaPredeterminado());
        equipo.setCategoria(dto.getCategoria());
        if (dto.getSucursalId() != null) {
            equipo.setSucursalId(dto.getSucursalId());
        }
        if (dto.getActivo() != null) {
            equipo.setActivo(dto.getActivo());
        }

        return mapToDto(equipoRepository.save(equipo));
    }

    @Transactional
    public List<EquipoDto> crearLote(List<EquipoDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }
        return dtos.stream()
                .filter(d -> d.getDescripcion() != null && !d.getDescripcion().trim().isEmpty())
                .map(this::crearOActualizar)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminar(Long id) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo", "id", id));
        equipo.setActivo(false);
        equipoRepository.save(equipo);
    }

    private EquipoDto mapToDto(Equipo entity) {
        return EquipoDto.builder()
                .id(entity.getId())
                .descripcion(entity.getDescripcion())
                .partNumber(entity.getPartNumber())
                .caracteristicas(entity.getCaracteristicas())
                .precioReferencial(entity.getPrecioReferencial())
                .tiempoEntregaPredeterminado(entity.getTiempoEntregaPredeterminado())
                .categoria(entity.getCategoria())
                .sucursalId(entity.getSucursalId())
                .activo(entity.getActivo())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }
}
