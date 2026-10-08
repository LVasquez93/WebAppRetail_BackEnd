package com.retail.cotizador.auth.security;

import com.retail.cotizador.usuarios.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final String nombreCompleto;
    private final String correo;
    private final String cargo;
    private final String rol;
    private final Long empresaId;
    private final Long sucursalId;
    private final boolean activo;
    private final Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(Usuario usuario) {
        String rol = usuario.getRol();
        if (rol == null || rol.trim().isEmpty()) {
            rol = "ROLE_VENTAS";
        }
        if (!rol.startsWith("ROLE_")) {
            rol = "ROLE_" + rol;
        }

        // Normalización y jerarquía de roles
        if ("ROLE_GERENTE".equals(rol)) {
            String cargoUpper = usuario.getCargo() != null ? usuario.getCargo().toUpperCase() : "";
            boolean esSedeEspecifica = cargoUpper.contains("SUCURSAL") || cargoUpper.contains("SEDE");
            rol = esSedeEspecifica ? "ROLE_GERENTE_SUCURSAL" : "ROLE_GERENTE_GENERAL";
        }

        List<GrantedAuthority> authorities = new java.util.ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(rol));
        if ("ROLE_ADMIN".equals(rol)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_GERENTE_GENERAL"));
            authorities.add(new SimpleGrantedAuthority("ROLE_GERENTE_SUCURSAL"));
            authorities.add(new SimpleGrantedAuthority("ROLE_GERENTE"));
            authorities.add(new SimpleGrantedAuthority("ROLE_VENTAS"));
        } else if ("ROLE_GERENTE_GENERAL".equals(rol)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_GERENTE"));
            authorities.add(new SimpleGrantedAuthority("ROLE_GERENTE_SUCURSAL"));
            authorities.add(new SimpleGrantedAuthority("ROLE_VENTAS"));
        } else if ("ROLE_GERENTE_SUCURSAL".equals(rol)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_GERENTE"));
            authorities.add(new SimpleGrantedAuthority("ROLE_VENTAS"));
        }

        return UserPrincipal.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .nombreCompleto(usuario.getNombreCompleto())
                .correo(usuario.getCorreo())
                .cargo(usuario.getCargo())
                .rol(rol)
                .empresaId(usuario.getEmpresaId())
                .sucursalId(usuario.getSucursalId())
                .activo(usuario.getActivo() != null && usuario.getActivo())
                .authorities(authorities)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}
