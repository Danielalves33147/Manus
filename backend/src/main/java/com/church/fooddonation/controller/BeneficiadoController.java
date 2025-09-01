package com.church.fooddonation.controller;

import com.church.fooddonation.dto.BeneficiadoDTO;
import com.church.fooddonation.service.BeneficiadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/beneficiados")
@Tag(name = "Beneficiados", description = "Gerenciamento de beneficiados")
@CrossOrigin(origins = "*")
public class BeneficiadoController {

    @Autowired
    private BeneficiadoService beneficiadoService;

    @GetMapping
    @Operation(summary = "Listar todos os beneficiados")
    public ResponseEntity<List<BeneficiadoDTO>> getAllBeneficiados() {
        List<BeneficiadoDTO> beneficiados = beneficiadoService.findAll();
        return ResponseEntity.ok(beneficiados);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar beneficiado por ID")
    public ResponseEntity<BeneficiadoDTO> getBeneficiadoById(@PathVariable UUID id) {
        return beneficiadoService.findById(id)
                .map(beneficiado -> ResponseEntity.ok(beneficiado))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar beneficiado por CPF")
    public ResponseEntity<BeneficiadoDTO> getBeneficiadoByCpf(@PathVariable String cpf) {
        return beneficiadoService.findByCpf(cpf)
                .map(beneficiado -> ResponseEntity.ok(beneficiado))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo beneficiado")
    public ResponseEntity<BeneficiadoDTO> createBeneficiado(@Valid @RequestBody BeneficiadoDTO beneficiadoDTO) {
        try {
            BeneficiadoDTO savedBeneficiado = beneficiadoService.save(beneficiadoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBeneficiado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar beneficiado existente")
    public ResponseEntity<BeneficiadoDTO> updateBeneficiado(@PathVariable UUID id, @Valid @RequestBody BeneficiadoDTO beneficiadoDTO) {
        try {
            return beneficiadoService.update(id, beneficiadoDTO)
                    .map(beneficiado -> ResponseEntity.ok(beneficiado))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir beneficiado")
    public ResponseEntity<Void> deleteBeneficiado(@PathVariable UUID id) {
        if (beneficiadoService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar beneficiados por nome")
    public ResponseEntity<List<BeneficiadoDTO>> getBeneficiadosByNome(@RequestParam String nome) {
        List<BeneficiadoDTO> beneficiados = beneficiadoService.findByNome(nome);
        return ResponseEntity.ok(beneficiados);
    }
}

