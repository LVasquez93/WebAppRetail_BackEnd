package com.retail.cotizador.empresas.service;

import com.retail.cotizador.common.exception.ResourceNotFoundException;
import com.retail.cotizador.empresas.dto.EmpresaDto;
import com.retail.cotizador.empresas.entity.Empresa;
import com.retail.cotizador.empresas.repository.EmpresaRepository;
import com.retail.cotizador.sucursales.entity.Sucursal;
import com.retail.cotizador.sucursales.repository.SucursalRepository;
import com.retail.cotizador.usuarios.entity.Usuario;
import com.retail.cotizador.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<EmpresaDto> listarTodas() {
        return empresaRepository.findAllByOrderByNombreAsc().stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmpresaDto> listarActivas() {
        return empresaRepository.findAllByActivoTrueOrderByNombreAsc().stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmpresaDto obtenerPorId(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", id));
        return mapearADto(empresa);
    }

    @Transactional
    public EmpresaDto crear(EmpresaDto dto) {
        Empresa empresa = Empresa.builder()
                .nombre(dto.getNombre().trim())
                .razonSocial(dto.getRazonSocial() != null ? dto.getRazonSocial().trim() : null)
                .nit(dto.getNit() != null ? dto.getNit().trim() : null)
                .telefono(dto.getTelefono())
                .correo(dto.getCorreo())
                .direccion(dto.getDireccion())
                .logoBase64(dto.getLogoBase64())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();

        Empresa guardada = empresaRepository.save(empresa);

        // 1. Auto-crear la sucursal matriz inicial de la nueva empresa
        Sucursal sucursalMatriz = Sucursal.builder()
                .codigo("SUC_" + guardada.getId() + "_01")
                .nombre(guardada.getNombre() + " (Casa Matriz)")
                .razonSocial(guardada.getRazonSocial() != null && !guardada.getRazonSocial().isBlank() ? guardada.getRazonSocial() : guardada.getNombre())
                .nombreComercial(guardada.getNombre())
                .direccion(guardada.getDireccion() != null && !guardada.getDireccion().isBlank() ? guardada.getDireccion() : "Oficina Central")
                .telefono(guardada.getTelefono())
                .correo(guardada.getCorreo())
                .prefijoCotizacion("COT" + guardada.getId())
                .nombreFirmante(dto.getGerenteNombreCompleto() != null && !dto.getGerenteNombreCompleto().isBlank()
                        ? dto.getGerenteNombreCompleto().trim().toUpperCase() : "GERENTE GENERAL")
                .cargoFirmante("GERENTE GENERAL")
                .formaPagoPredeterminada("Crédito 30 días, Transferencia Bancaria o Cheque")
                .notaPredeterminada("** IMPORTANTE ** Tiempos de entrega y precios, podrían estar sujetos a cambios en inventario")
                .empresaId(guardada.getId())
                .activo(true)
                .build();
        Sucursal sucursalGuardada = sucursalRepository.save(sucursalMatriz);
        log.info("Sucursal matriz inicial creada para empresa {}: {}", guardada.getNombre(), sucursalGuardada.getNombre());

        // 2. Si se enviaron datos para el Gerente inicial de la empresa, crearlo automáticamente
        if (dto.getGerenteUsername() != null && !dto.getGerenteUsername().isBlank()
                && dto.getGerentePassword() != null && !dto.getGerentePassword().isBlank()) {
            String username = dto.getGerenteUsername().trim().toUpperCase();
            if (usuarioRepository.findByUsername(username).isEmpty()) {
                Usuario gerente = Usuario.builder()
                        .username(username)
                        .password(passwordEncoder.encode(dto.getGerentePassword().trim()))
                        .nombreCompleto(dto.getGerenteNombreCompleto() != null && !dto.getGerenteNombreCompleto().isBlank()
                                ? dto.getGerenteNombreCompleto().trim() : "GERENTE " + guardada.getNombre())
                        .correo(dto.getGerenteCorreo())
                        .cargo("GERENTE GENERAL / DUEÑO")
                        .rol("ROLE_GERENTE_GENERAL")
                        .empresaId(guardada.getId())
                        .sucursalId(sucursalGuardada.getId())
                        .activo(true)
                        .build();
                usuarioRepository.save(gerente);
                log.info("Gerente inicial {} creado con éxito para la empresa {}", username, guardada.getNombre());
            }
        }

        return mapearADto(guardada);
    }

    @Transactional
    public EmpresaDto actualizar(Long id, EmpresaDto dto) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", id));

        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            empresa.setNombre(dto.getNombre().trim());
        }
        empresa.setRazonSocial(dto.getRazonSocial());
        empresa.setNit(dto.getNit());
        empresa.setTelefono(dto.getTelefono());
        empresa.setCorreo(dto.getCorreo());
        empresa.setDireccion(dto.getDireccion());
        if (dto.getLogoBase64() != null) {
            empresa.setLogoBase64(dto.getLogoBase64());
        }
        if (dto.getActivo() != null) {
            empresa.setActivo(dto.getActivo());
        }

        Empresa empresaActualizada = empresaRepository.save(empresa);

        // Actualizar o aprovisionar el Gerente de la empresa si se enviaron datos
        if (dto.getGerenteUsername() != null && !dto.getGerenteUsername().isBlank()) {
            String username = dto.getGerenteUsername().trim().toUpperCase();
            Usuario gerente = usuarioRepository.findByEmpresaId(empresaActualizada.getId()).stream()
                    .filter(u -> "ROLE_GERENTE_GENERAL".equals(u.getRol()) || "ROLE_GERENTE".equals(u.getRol()))
                    .findFirst()
                    .orElse(null);

            if (gerente != null) {
                if (!gerente.getUsername().equalsIgnoreCase(username)) {
                    if (usuarioRepository.findByUsername(username).isEmpty()) {
                        gerente.setUsername(username);
                    }
                }
                if (dto.getGerenteNombreCompleto() != null && !dto.getGerenteNombreCompleto().isBlank()) {
                    gerente.setNombreCompleto(dto.getGerenteNombreCompleto().trim());
                }
                if (dto.getGerenteCorreo() != null) {
                    gerente.setCorreo(dto.getGerenteCorreo().trim());
                }
                if (dto.getGerentePassword() != null && !dto.getGerentePassword().isBlank()) {
                    gerente.setPassword(passwordEncoder.encode(dto.getGerentePassword().trim()));
                }
                gerente.setRol("ROLE_GERENTE_GENERAL");
                usuarioRepository.save(gerente);
                log.info("Gerente de empresa {} actualizado exitosamente", empresaActualizada.getNombre());
            } else if (dto.getGerentePassword() != null && !dto.getGerentePassword().isBlank()) {
                Long sucursalId = sucursalRepository.findAllByEmpresaIdOrderByIdAsc(empresaActualizada.getId()).stream()
                        .findFirst().map(Sucursal::getId).orElse(null);

                Usuario nuevoGerente = Usuario.builder()
                        .username(username)
                        .password(passwordEncoder.encode(dto.getGerentePassword().trim()))
                        .nombreCompleto(dto.getGerenteNombreCompleto() != null && !dto.getGerenteNombreCompleto().isBlank()
                                ? dto.getGerenteNombreCompleto().trim() : "GERENTE " + empresaActualizada.getNombre())
                        .correo(dto.getGerenteCorreo())
                        .cargo("GERENTE GENERAL / DUEÑO")
                        .rol("ROLE_GERENTE_GENERAL")
                        .empresaId(empresaActualizada.getId())
                        .sucursalId(sucursalId)
                        .activo(true)
                        .build();
                usuarioRepository.save(nuevoGerente);
                log.info("Nuevo gerente creado para empresa {}: {}", empresaActualizada.getNombre(), username);
            }
        }

        return mapearADto(empresaActualizada);
    }

    @Transactional
    public void cambiarEstado(Long id, boolean activo) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", id));
        empresa.setActivo(activo);
        empresaRepository.save(empresa);
    }

    private EmpresaDto mapearADto(Empresa e) {
        Usuario gerente = usuarioRepository.findByEmpresaId(e.getId()).stream()
                .filter(u -> "ROLE_GERENTE_GENERAL".equals(u.getRol()) || "ROLE_GERENTE".equals(u.getRol()))
                .findFirst()
                .orElse(null);

        return EmpresaDto.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .razonSocial(e.getRazonSocial())
                .nit(e.getNit())
                .telefono(e.getTelefono())
                .correo(e.getCorreo())
                .direccion(e.getDireccion())
                .logoBase64(e.getLogoBase64())
                .activo(e.getActivo())
                .fechaCreacion(e.getFechaCreacion())
                .gerenteId(gerente != null ? gerente.getId() : null)
                .gerenteUsername(gerente != null ? gerente.getUsername() : null)
                .gerenteNombreCompleto(gerente != null ? gerente.getNombreCompleto() : null)
                .gerenteCorreo(gerente != null ? gerente.getCorreo() : null)
                .build();
    }
}
