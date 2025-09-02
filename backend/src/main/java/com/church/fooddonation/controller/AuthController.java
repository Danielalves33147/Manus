package com.church.fooddonation.controller;

import com.church.fooddonation.entity.Usuario;
import com.church.fooddonation.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Realizar login no sistema")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            
            Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
            
            if (usuario != null && passwordEncoder.matches(password, usuario.getPassword())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("username", usuario.getUsername());
                response.put("role", usuario.getRole());
                response.put("message", "Login realizado com sucesso");
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Credenciais inválidas");
                
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erro interno do servidor: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/init")
    @Operation(summary = "Inicializar usuário administrador padrão")
    public ResponseEntity<?> initializeAdmin() {
        try {
            if (!usuarioRepository.existsByUsername("admin")) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ADMIN");
                admin.setEmail("admin@fooddonation.com");
                usuarioRepository.save(admin);
                
                return ResponseEntity.ok("Usuário administrador criado com sucesso! Username: admin, Password: admin123");
            }
            return ResponseEntity.ok("Usuário administrador já existe");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao criar usuário administrador: " + e.getMessage());
        }
    }
}

