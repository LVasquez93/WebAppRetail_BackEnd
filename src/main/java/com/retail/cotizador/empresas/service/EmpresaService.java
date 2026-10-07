package com.retail.cotizador.empresas.service;

import com.retail.cotizador.common.exception.ResourceNotFoundException;
import com.retail.cotizador.empresas.dto.EmpresaDto;
import com.retail.cotizador.empresas.entity.Empresa;
import com.retail.cotizador.empresas.repository.EmpresaRepository;
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

        // Si se enviaron datos para el Gerente inicial de la empresa, crearlo automáticamente
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
                        .rol("ROLE_GERENTE")
                        .empresaId(guardada.getId())
                        .sucursalId(null) // El gerente supervisa todas las sucursales de su empresa
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

        return mapearADto(empresaRepository.save(empresa));
    }

    @Transactional
    public void cambiarEstado(Long id, boolean activo) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", id));
        empresa.setActivo(activo);
        empresaRepository.save(empresa);
    }

    private EmpresaDto mapearADto(Empresa e) {
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
                .build();
    }
}
