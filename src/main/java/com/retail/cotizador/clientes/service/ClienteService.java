package com.retail.cotizador.clientes.service;

import com.retail.cotizador.clientes.dto.ClienteDto;
import com.retail.cotizador.clientes.entity.Cliente;
import com.retail.cotizador.clientes.repository.ClienteRepository;
import com.retail.cotizador.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<ClienteDto> listarOBuscar(String query) {
        return listarOBuscar(query, null);
    }

    @Transactional(readOnly = true)
    public List<ClienteDto> listarOBuscar(String query, Long sucursalId) {
        List<Cliente> list;
        if (query != null && !query.trim().isEmpty()) {
            list = clienteRepository.buscarClientes(query.trim(), sucursalId);
        } else {
            list = clienteRepository.listarPorSucursal(sucursalId);
        }
        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClienteDto obtenerPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        return mapToDto(cliente);
    }

    @Transactional
    public ClienteDto crear(ClienteDto dto) {
        Cliente cliente = Cliente.builder()
                .razonSocial(dto.getRazonSocial().trim())
                .nombreComercial(dto.getNombreComercial() != null ? dto.getNombreComercial().trim() : null)
                .contactoPrincipal(dto.getContactoPrincipal() != null ? dto.getContactoPrincipal().trim() : null)
                .telefono(dto.getTelefono())
                .correo(dto.getCorreo())
                .direccion(dto.getDireccion())
                .sucursalId(dto.getSucursalId())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();

        return mapToDto(clienteRepository.save(cliente));
    }

    @Transactional
    public ClienteDto actualizar(Long id, ClienteDto dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        cliente.setRazonSocial(dto.getRazonSocial().trim());
        cliente.setNombreComercial(dto.getNombreComercial() != null ? dto.getNombreComercial().trim() : null);
        cliente.setContactoPrincipal(dto.getContactoPrincipal() != null ? dto.getContactoPrincipal().trim() : null);
        cliente.setTelefono(dto.getTelefono());
        cliente.setCorreo(dto.getCorreo());
        cliente.setDireccion(dto.getDireccion());
        if (dto.getSucursalId() != null) {
            cliente.setSucursalId(dto.getSucursalId());
        }
        if (dto.getActivo() != null) {
            cliente.setActivo(dto.getActivo());
        }

        return mapToDto(clienteRepository.save(cliente));
    }

    @Transactional
    public List<ClienteDto> crearLote(List<ClienteDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }
        return dtos.stream()
                .filter(d -> d.getRazonSocial() != null && !d.getRazonSocial().trim().isEmpty())
                .map(this::crear)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        cliente.setActivo(false);
        clienteRepository.save(cliente);
    }

    private ClienteDto mapToDto(Cliente entity) {
        return ClienteDto.builder()
                .id(entity.getId())
                .razonSocial(entity.getRazonSocial())
                .nombreComercial(entity.getNombreComercial())
                .contactoPrincipal(entity.getContactoPrincipal())
                .telefono(entity.getTelefono())
                .correo(entity.getCorreo())
                .direccion(entity.getDireccion())
                .sucursalId(entity.getSucursalId())
                .activo(entity.getActivo())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }
}
