package com.church.fooddonation.config;

import com.church.fooddonation.entity.Usuario;
import com.church.fooddonation.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verificar se já existe um usuário admin
        if (!usuarioRepository.existsByUsername("admin")) {
            // Criar usuário admin padrão
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@sistema.com");
            admin.setRole("ADMIN");
            
            usuarioRepository.save(admin);
            System.out.println("✅ Usuário administrador criado automaticamente:");
            System.out.println("   Usuário: admin");
            System.out.println("   Senha: admin123");
            System.out.println("   Role: ADMIN");
        } else {
            System.out.println("✅ Usuário administrador já existe no sistema");
        }
    }
}

