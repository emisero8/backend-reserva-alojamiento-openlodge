package com.example.openlodge.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.openlodge.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Este es el único método que Spring Security llamará.
     * "username" será el email que le pasemos.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // 1. Buscamos nuestro Usuario en nuestra BD
        var usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + username));

        // 2. Creamos la lista de "roles" (autoridades)
        // Usamos el campo 'rol' que ya teníamos
        List<SimpleGrantedAuthority> authorities = 
                Collections.singletonList(new SimpleGrantedAuthority(usuario.getRol()));

        // 3. Devolvemos un "User" de Spring Security
        // Spring usará esto para comparar la contraseña
        return new User (
                usuario.getEmail(),
                usuario.getPassword(),
                authorities
        );
    }
}
