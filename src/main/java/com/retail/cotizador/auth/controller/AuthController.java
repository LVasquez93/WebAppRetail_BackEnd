package com.retail.cotizador.auth.controller;

import com.retail.cotizador.auth.dto.AuthResponseDto;
import com.retail.cotizador.auth.dto.LoginRequestDto;
import com.retail.cotizador.auth.security.UserPrincipal;
import com.retail.cotizador.auth.service.JwtService;
import com.retail.cotizador.sucursales.entity.Sucursal;
import com.retail.cotizador.sucursales.repository.SucursalRepository;
import com.retail.cotizador.usuarios.entity.Usuario;
import com.retail.cotizador.usuarios.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        String username = request.getUsername().trim().toUpperCase();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado tras autenticación"));

        Sucursal sucursal = null;
        if (usuario.getSucursalId() != null) {
            sucursal = sucursalRepository.findById(usuario.getSucursalId()).orElse(null);
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", usuario.getId());
        claims.put("rol", usuario.getRol());
        claims.put("sucursalId", usuario.getSucursalId());
        claims.put("nombreCompleto", usuario.getNombreCompleto());

        String jwt = jwtService.generateToken(principal, claims);

        AuthResponseDto response = AuthResponseDto.builder()
                .token(jwt)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationMs())
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombreCompleto(usuario.getNombreCompleto())
                .correo(usuario.getCorreo())
                .cargo(usuario.getCargo())
                .rol(usuario.getRol())
                .sucursalId(usuario.getSucursalId())
                .sucursalCodigo(sucursal != null ? sucursal.getCodigo() : null)
                .sucursalNombre(sucursal != null ? sucursal.getNombre() : null)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponseDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return ResponseEntity.status(401).build();
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(principal.getUsername());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioOpt.get();
        Sucursal sucursal = null;
        if (usuario.getSucursalId() != null) {
            sucursal = sucursalRepository.findById(usuario.getSucursalId()).orElse(null);
        }

        AuthResponseDto response = AuthResponseDto.builder()
                .token(null) // Token no regenerado aquí
                .tokenType("Bearer")
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombreCompleto(usuario.getNombreCompleto())
                .correo(usuario.getCorreo())
                .cargo(usuario.getCargo())
                .rol(usuario.getRol())
                .sucursalId(usuario.getSucursalId())
                .sucursalCodigo(sucursal != null ? sucursal.getCodigo() : null)
                .sucursalNombre(sucursal != null ? sucursal.getNombre() : null)
                .build();

        return ResponseEntity.ok(response);
    }
}
