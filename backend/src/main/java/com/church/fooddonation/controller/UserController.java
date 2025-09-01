package com.church.fooddonation.controller;

import com.church.fooddonation.dto.CreateUserRequest;
import com.church.fooddonation.entity.Usuario;
import com.church.fooddonation.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Tag(name = "Gerenciamento de Usuários", description = "Endpoints para gerenciamento de usuários (apenas admin)")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    @Operation(summary = "Criar novo usuário (apenas admin)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            // Verificar se username já existe
            if (usuarioRepository.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest().body("Username já existe");
            }

            // Verificar se email já existe (se fornecido)
            if (request.getEmail() != null && !request.getEmail().isEmpty() && 
                usuarioRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body("Email já existe");
            }

            // Validar role
            if (!isValidRole(request.getRole())) {
                return ResponseEntity.badRequest().body("Role inválido. Use: USER ou ADMIN");
            }

            Usuario usuario = new Usuario();
            usuario.setUsername(request.getUsername());
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            usuario.setEmail(request.getEmail());
            usuario.setRole(request.getRole().toUpperCase());

            Usuario savedUser = usuarioRepository.save(usuario);

            // Retornar dados do usuário sem a senha
            return ResponseEntity.ok(new UserResponse(
                savedUser.getId().toString(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao criar usuário: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos os usuários (apenas admin)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<UserResponse> response = usuarios.stream()
            .map(u -> new UserResponse(
                u.getId().toString(),
                u.getUsername(),
                u.getEmail(),
                u.getRole()
            ))
            .toList();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Obter dados do usuário logado")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        if (usuario.isPresent()) {
            Usuario u = usuario.get();
            return ResponseEntity.ok(new UserResponse(
                u.getId().toString(),
                u.getUsername(),
                u.getEmail(),
                u.getRole()
            ));
        }
        
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Deletar usuário (apenas admin)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            
            Optional<Usuario> userToDelete = usuarioRepository.findById(java.util.UUID.fromString(userId));
            if (userToDelete.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // Não permitir que admin delete a si mesmo
            if (userToDelete.get().getUsername().equals(currentUsername)) {
                return ResponseEntity.badRequest().body("Não é possível deletar seu próprio usuário");
            }
            
            usuarioRepository.deleteById(java.util.UUID.fromString(userId));
            return ResponseEntity.ok("Usuário deletado com sucesso");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao deletar usuário: " + e.getMessage());
        }
    }

    private boolean isValidRole(String role) {
        return "USER".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);
    }

    // Classe interna para resposta
    public static class UserResponse {
        private String id;
        private String username;
        private String email;
        private String role;

        public UserResponse(String id, String username, String email, String role) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.role = role;
        }

        // Getters
        public String getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
    }
}

