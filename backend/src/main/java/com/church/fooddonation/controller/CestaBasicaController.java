package com.church.fooddonation.controller;

import com.church.fooddonation.entity.CestaBasica;
import com.church.fooddonation.service.CestaBasicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/cestas-basicas")
@CrossOrigin(origins = "*")
@Tag(name = "Cestas Básicas", description = "Operações relacionadas ao gerenciamento de cestas básicas")
public class CestaBasicaController {

    @Autowired
    private CestaBasicaService cestaBasicaService;

    @GetMapping
    @Operation(summary = "Listar todas as cestas básicas")
    public ResponseEntity<List<CestaBasica>> listarTodasCestas() {
        try {
            List<CestaBasica> cestas = cestaBasicaService.listarTodasCestas();
            return ResponseEntity.ok(cestas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cesta básica por ID")
    public ResponseEntity<CestaBasica> buscarPorId(@PathVariable UUID id) {
        try {
            Optional<CestaBasica> cesta = cestaBasicaService.buscarPorId(id);
            return cesta.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Buscar cestas básicas por status")
    public ResponseEntity<List<CestaBasica>> buscarPorStatus(@PathVariable String status) {
        try {
            List<CestaBasica> cestas = cestaBasicaService.buscarPorStatus(status);
            return ResponseEntity.ok(cestas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "Buscar cestas básicas montadas e disponíveis")
    public ResponseEntity<List<CestaBasica>> buscarCestasDisponiveis() {
        try {
            List<CestaBasica> cestas = cestaBasicaService.buscarCestasMontadasDisponiveis();
            return ResponseEntity.ok(cestas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @Operation(summary = "Criar nova cesta básica")
    public ResponseEntity<CestaBasica> criarCesta(@RequestBody Map<String, String> request) {
        try {
            String nomeCesta = request.get("nomeCesta");
            if (nomeCesta == null || nomeCesta.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            CestaBasica cesta = cestaBasicaService.criarCesta(nomeCesta);
            return ResponseEntity.status(HttpStatus.CREATED).body(cesta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{cestaId}/itens")
    @Operation(summary = "Adicionar item à cesta básica")
    public ResponseEntity<CestaBasica> adicionarItem(
            @PathVariable UUID cestaId,
            @RequestBody Map<String, Object> request) {
        try {
            UUID alimentoEstoqueId = UUID.fromString((String) request.get("alimentoEstoqueId"));
            Double quantidade = Double.valueOf(request.get("quantidade").toString());
            
            CestaBasica cesta = cestaBasicaService.adicionarItemACesta(cestaId, alimentoEstoqueId, quantidade);
            return ResponseEntity.ok(cesta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{cestaId}/finalizar")
    @Operation(summary = "Finalizar montagem da cesta básica")
    public ResponseEntity<CestaBasica> finalizarMontagem(@PathVariable UUID cestaId) {
        try {
            CestaBasica cesta = cestaBasicaService.finalizarMontagem(cestaId);
            return ResponseEntity.ok(cesta);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir cesta básica")
    public ResponseEntity<Void> excluirCesta(@PathVariable UUID id) {
        try {
            cestaBasicaService.excluirCesta(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/estatisticas/{status}")
    @Operation(summary = "Contar cestas básicas por status")
    public ResponseEntity<Map<String, Long>> contarPorStatus(@PathVariable String status) {
        try {
            Long count = cestaBasicaService.contarPorStatus(status);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

