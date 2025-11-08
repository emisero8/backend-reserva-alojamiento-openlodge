package com.example.openlodge.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.openlodge.service.JwtService;
import com.example.openlodge.service.UserDetailsServiceImpl;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public JwtAuthFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Este método es el "guardia". Se ejecuta en CADA petición
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 1. Obtenemos el "Header" de autorización
        final String authHeader = request.getHeader("Authorization");

        // 2. Si no hay header o no empieza con "Bearer ", lo dejamos pasar
        //    (será una ruta pública o será bloqueado más adelante)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraemos el token (quitando "Bearer")
        final String token = authHeader.substring(7);
        final String userEmail;

        try {
            userEmail = jwtService.extractUsername(token);
        } catch (Exception e) {
            // Si el token es inválido (expirado, malformado), lo dejamos pasar
            // y Spring lo bloqueará por ser "Anónimo"
            filterChain.doFilter(request, response);
            return;
        }

        // 4. Si tenemos email Y el usuario no está "logueado" en esta petición
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 5. cargamos el usuario desde la BD
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 6. validamos el token
            if (jwtService.isTokenValid(token, userDetails)) {
                
                // Creamos la "sesión" de autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // No necesitamos credenciales (contraseña)
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Guardamos la autenticación en el Contexto de Seguridad
                // Esto es lo que le dice a Spring que el usuario está logueado
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 7. Pasamos al siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }
}
