package com.example.openlodge.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.openlodge.service.UserDetailsServiceImpl;

@Configuration // ⬅️ Le dice a Spring que esta es una clase de configuración
@EnableWebSecurity // ⬅️ Habilita la seguridad web de Spring
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtAuthFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Creamos el "Encriptador" de contraseñas.
     * Usamos BCrypt, que es el estándar de la industria.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * ¡ESTE ES EL BEAN QUE FALTABA!
     * Este es el "Gerente" de Autenticación.
     * Lo inyectaremos en nuestro controlador de Login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Este es el "Proveedor" de Autenticación.
     * Le dice a Spring que use nuestro UserDetailsServiceImpl (para encontrar al
     * usuario)
     * y nuestro PasswordEncoder (para comparar las contraseñas).
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService); // Esta advertencia debería desaparecer
        return authProvider;
    }

    /**
     * Configuramos las "reglas" de seguridad de la API.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Deshabilitamos CSRF (común en APIs REST stateless)
                .csrf(csrf -> csrf.disable())

                // ¡LÍNEA AGREGADA PARA EVITAR LAS CAJAS DE CONEXION RECHAZADA EN LOCALHOST!
                // H2 Console usa frames, y Spring Security los bloquea por defecto.
                // Esta línea deshabilita esa protección (X-Frame-Options: DENY).
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))

                // 2. Le decimos a Spring que no maneje sesiones (somos stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // LE DECIMOS A SPRING QUE USE NUESTRO "PROVEEDOR"
                .authenticationProvider(authenticationProvider())

                // Le decimos a Spring: "Ejecuta nuestro 'JwtAuthFilter'
                // ANTES del filtro normal de usuario y contraseña"
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // 3. Definimos las reglas de autorización (quién puede ver qué)
                .authorizeHttpRequests(authz -> authz

                        // RUTAS PÚBLICAS (INCLUYENDO /LOGIN)
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll() // ⬅️ Para registrarse
                        .requestMatchers(HttpMethod.GET, "/api/propiedades/**").permitAll() // ⬅️ Ver todas o una
                                                                                            // propiedad
                        .requestMatchers("/h2-console/**").permitAll() // ⬅️ Permitir acceso a la consola H2

                        // Todo lo demás (el resto de endpoints) requiere autenticación
                        .anyRequest().authenticated());

        return http.build();
    }
}
