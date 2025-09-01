package com.church.fooddonation.controller;

import com.church.fooddonation.dto.JwtResponse;
import com.church.fooddonation.dto.LoginRequest;
import com.church.fooddonation.entity.Usuario;
import com.church.fooddonation.repository.UsuarioRepository;
import com.church.fooddonation.security.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Realizar login no sistema")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            final String token = jwtTokenUtil.generateToken(userDetails);
            
            Usuario usuario = usuarioRepository.findByUsername(loginRequest.getUsername()).orElse(null);
            String role = usuario != null ? usuario.getRole() : "USER";

            return ResponseEntity.ok(new JwtResponse(token, userDetails.getUsername(), role));
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body("Credenciais inválidas");
        }
    }

    @PostMapping("/init")
    @Operation(summary = "Inicializar usuário administrador padrão")
    public ResponseEntity<?> initializeAdmin() {
        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setEmail("admin@fooddonation.com");
            usuarioRepository.save(admin);
            return ResponseEntity.ok("Usuário administrador criado com sucesso");
        }
        return ResponseEntity.ok("Usuário administrador já existe");
    }
}

