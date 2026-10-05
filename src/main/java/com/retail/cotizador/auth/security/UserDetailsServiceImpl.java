package com.retail.cotizador.auth.security;

import com.retail.cotizador.usuarios.entity.Usuario;
import com.retail.cotizador.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username.trim().toUpperCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));

        return UserPrincipal.create(usuario);
    }
}
