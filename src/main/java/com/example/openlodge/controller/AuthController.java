package com.example.openlodge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.openlodge.dto.AuthResponse;
import com.example.openlodge.dto.LoginRequest;
import com.example.openlodge.model.Usuario;
import com.example.openlodge.repository.UsuarioRepository;
import com.example.openlodge.service.JwtService;
import com.example.openlodge.service.UserDetailsServiceImpl;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/auth") // URL base para autenticación
@CrossOrigin(origins = "http://localhost:8081")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, 
                          UserDetailsServiceImpl userDetailsService, 
                          JwtService jwtService,
                          UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Endpoint para LOGIN (Iniciar Sesión).
     * Se activa con: POST http://localhost:8080/api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        
        // 1. Autenticamos al usuario
        // Esto usa el AuthenticationManager que configuramos en SecurityConfig.
        // Internamente, llamará a UserDetailsServiceImpl y PasswordEncoder.
        // Si el email o la contraseña son incorrectos, lanzará una excepción.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), 
                        loginRequest.getPassword()
                )
        );

        // 2. Si la autenticación fue exitosa, cargamos los detalles del usuario
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(loginRequest.getEmail());

        // 3. Generamos el token JWT
        final String token = jwtService.generateToken(userDetails);

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new EntityNotFoundException("Error post-autenticación: Usuario no encontrado"));

        // 4. Obtenemos el rol desde los UserDetails
        //final String rol = userDetails.getAuthorities().stream()
        //        .map(GrantedAuthority::getAuthority)
        //        .findFirst()
        //        .orElseThrow(() -> new RuntimeException("Usuario no tiene rol."));

        // 5. Devolvemos el token en la respuesta
        return ResponseEntity.ok(new AuthResponse(
            token, 
            usuario.getRol(), 
            usuario.getNombre(), 
            usuario.getApellido(),
            usuario.getEmail()
        ));
    }
}
