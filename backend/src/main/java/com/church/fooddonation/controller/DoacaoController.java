package com.church.fooddonation.controller;

import com.church.fooddonation.dto.DoacaoDTO;
import com.church.fooddonation.service.DoacaoService;
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
@RequestMapping("/doacoes")
@Tag(name = "Doações", description = "Gerenciamento de doações de alimentos")
@CrossOrigin(origins = "*")
public class DoacaoController {

    @Autowired
    private DoacaoService doacaoService;

    @GetMapping
    @Operation(summary = "Listar todas as doações")
    public ResponseEntity<List<DoacaoDTO>> getAllDoacoes() {
        List<DoacaoDTO> doacoes = doacaoService.findAll();
        return ResponseEntity.ok(doacoes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar doação por ID")
    public ResponseEntity<DoacaoDTO> getDoacaoById(@PathVariable UUID id) {
        return doacaoService.findById(id)
                .map(doacao -> ResponseEntity.ok(doacao))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Registrar nova doação")
    public ResponseEntity<DoacaoDTO> createDoacao(@Valid @RequestBody DoacaoDTO doacaoDTO) {
        DoacaoDTO savedDoacao = doacaoService.save(doacaoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDoacao);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar doação existente")
    public ResponseEntity<DoacaoDTO> updateDoacao(@PathVariable UUID id, @Valid @RequestBody DoacaoDTO doacaoDTO) {
        return doacaoService.update(id, doacaoDTO)
                .map(doacao -> ResponseEntity.ok(doacao))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir doação")
    public ResponseEntity<Void> deleteDoacao(@PathVariable UUID id) {
        if (doacaoService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/tipo/{tipoAlimento}")
    @Operation(summary = "Buscar doações por tipo de alimento")
    public ResponseEntity<List<DoacaoDTO>> getDoacoesByTipo(@PathVariable String tipoAlimento) {
        List<DoacaoDTO> doacoes = doacaoService.findByTipoAlimento(tipoAlimento);
        return ResponseEntity.ok(doacoes);
    }

    @GetMapping("/proximas-vencimento")
    @Operation(summary = "Buscar doações próximas ao vencimento")
    public ResponseEntity<List<DoacaoDTO>> getDoacoesProximasVencimento(@RequestParam(defaultValue = "7") int dias) {
        List<DoacaoDTO> doacoes = doacaoService.findDoacoesProximasAoVencimento(dias);
        return ResponseEntity.ok(doacoes);
    }
}

