package com.church.fooddonation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank(message = "Username é obrigatório")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password é obrigatório")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Role é obrigatório")
    @Column(nullable = false, length = 50)
    private String role;

    @Email(message = "Email deve ter formato válido")
    @Column(unique = true)
    private String email;

    // Constructors
    public Usuario() {}

    public Usuario(String username, String password, String role, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

