package com.retail.cotizador.usuarios.service;

import com.retail.cotizador.common.exception.ResourceNotFoundException;
import com.retail.cotizador.usuarios.dto.UsuarioDto;
import com.retail.cotizador.usuarios.entity.Usuario;
import com.retail.cotizador.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioDto> listarOBuscar(String query) {
        return listarOBuscar(query, null, null);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDto> listarOBuscar(String query, Long empresaId) {
        return listarOBuscar(query, empresaId, null);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDto> listarOBuscar(String query, Long empresaId, Boolean soloAdmins) {
        List<Usuario> list;
        if (Boolean.TRUE.equals(soloAdmins)) {
            list = usuarioRepository.findByRolAndActivoTrueOrderByNombreCompletoAsc("ROLE_ADMIN");
            if (query != null && !query.trim().isEmpty()) {
                String q = query.trim().toLowerCase();
                list = list.stream().filter(u ->
                        (u.getNombreCompleto() != null && u.getNombreCompleto().toLowerCase().contains(q)) ||
                        (u.getUsername() != null && u.getUsername().toLowerCase().contains(q)) ||
                        (u.getCargo() != null && u.getCargo().toLowerCase().contains(q))
                ).collect(Collectors.toList());
            }
        } else if (empresaId != null) {
            list = usuarioRepository.findByEmpresaIdAndActivoTrueOrderByNombreCompletoAsc(empresaId);
            if (query != null && !query.trim().isEmpty()) {
                String q = query.trim().toLowerCase();
                list = list.stream().filter(u ->
                        (u.getNombreCompleto() != null && u.getNombreCompleto().toLowerCase().contains(q)) ||
                        (u.getUsername() != null && u.getUsername().toLowerCase().contains(q)) ||
                        (u.getCargo() != null && u.getCargo().toLowerCase().contains(q))
                ).collect(Collectors.toList());
            }
        } else if (query != null && !query.trim().isEmpty()) {
            list = usuarioRepository.buscarUsuarios(query.trim());
        } else {
            list = usuarioRepository.findByActivoTrueOrderByNombreCompletoAsc();
        }
        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioDto obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return mapToDto(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioDto obtenerPorUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));
        return mapToDto(usuario);
    }

    @Transactional
    public UsuarioDto crear(UsuarioDto dto) {
        String rawPassword = dto.getPassword();
        String encodedPassword = (rawPassword != null && !rawPassword.trim().isEmpty())
                ? passwordEncoder.encode(rawPassword.trim())
                : passwordEncoder.encode("123456");

        String rol = dto.getRol() != null && !dto.getRol().trim().isEmpty() ? dto.getRol().trim() : "ROLE_VENTAS";
        Long empresaId = "ROLE_ADMIN".equalsIgnoreCase(rol) ? null : dto.getEmpresaId();
        Long sucursalId = "ROLE_ADMIN".equalsIgnoreCase(rol) ? null : dto.getSucursalId();

        Usuario usuario = Usuario.builder()
                .username(dto.getUsername().trim().toUpperCase())
                .password(encodedPassword)
                .nombreCompleto(dto.getNombreCompleto().trim())
                .correo(dto.getCorreo())
                .cargo(dto.getCargo())
                .rol(rol)
                .empresaId(empresaId)
                .sucursalId(sucursalId)
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();

        return mapToDto(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioDto actualizar(Long id, UsuarioDto dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        usuario.setUsername(dto.getUsername().trim().toUpperCase());
        usuario.setNombreCompleto(dto.getNombreCompleto().trim());
        usuario.setCorreo(dto.getCorreo());
        usuario.setCargo(dto.getCargo());
        if (dto.getRol() != null && !dto.getRol().trim().isEmpty()) {
            usuario.setRol(dto.getRol().trim());
        }
        if ("ROLE_ADMIN".equalsIgnoreCase(usuario.getRol())) {
            usuario.setEmpresaId(null);
            usuario.setSucursalId(null);
        } else {
            usuario.setEmpresaId(dto.getEmpresaId());
            usuario.setSucursalId(dto.getSucursalId());
        }
        if (dto.getActivo() != null) {
            usuario.setActivo(dto.getActivo());
        }
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword().trim()));
        }

        return mapToDto(usuarioRepository.save(usuario));
    }

    @Transactional
    public List<UsuarioDto> crearLote(List<UsuarioDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }
        return dtos.stream()
                .filter(d -> d.getUsername() != null && !d.getUsername().trim().isEmpty() &&
                             d.getNombreCompleto() != null && !d.getNombreCompleto().trim().isEmpty())
                .map(this::crear)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    private UsuarioDto mapToDto(Usuario entity) {
        boolean tienePersonalizados = entity.getPermisosPersonalizadosJson() != null &&
                !entity.getPermisosPersonalizadosJson().trim().isEmpty();

        return UsuarioDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .nombreCompleto(entity.getNombreCompleto())
                .correo(entity.getCorreo())
                .cargo(entity.getCargo())
                .rol(entity.getRol())
                .empresaId(entity.getEmpresaId())
                .sucursalId(entity.getSucursalId())
                .tienePermisosPersonalizados(tienePersonalizados)
                .activo(entity.getActivo())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }
}
