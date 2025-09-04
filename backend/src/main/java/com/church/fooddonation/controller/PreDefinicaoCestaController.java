package com.church.fooddonation.controller;

import com.church.fooddonation.entity.PreDefinicaoCesta;
import com.church.fooddonation.entity.ItemPreDefinicao;
import com.church.fooddonation.repository.PreDefinicaoCestaRepository;
import com.church.fooddonation.repository.ItemPreDefinicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/pre-definicoes")
@CrossOrigin(origins = "*")
public class PreDefinicaoCestaController {

    @Autowired
    private PreDefinicaoCestaRepository preDefinicaoRepository;

    @Autowired
    private ItemPreDefinicaoRepository itemRepository;

    @GetMapping
    public ResponseEntity<List<PreDefinicaoCesta>> listarPreDefinicoes() {
        try {
            List<PreDefinicaoCesta> preDefinicoes = preDefinicaoRepository.findAllAtivasWithItens();
            return ResponseEntity.ok(preDefinicoes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criarPreDefinicao(@RequestBody Map<String, Object> dados) {
        try {
            String nome = (String) dados.get("nome");
            String descricao = (String) dados.get("descricao");
            
            PreDefinicaoCesta preDefinicao = new PreDefinicaoCesta(nome, descricao);
            preDefinicao = preDefinicaoRepository.save(preDefinicao);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Pré-definição criada com sucesso",
                "id", preDefinicao.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Erro ao criar pré-definição: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/itens")
    public ResponseEntity<Map<String, Object>> adicionarItem(@PathVariable UUID id, @RequestBody Map<String, Object> dados) {
        try {
            PreDefinicaoCesta preDefinicao = preDefinicaoRepository.findById(id).orElse(null);
            if (preDefinicao == null) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Pré-definição não encontrada"
                ));
            }

            String tipoAlimento = (String) dados.get("tipoAlimento");
            BigDecimal quantidade = new BigDecimal(dados.get("quantidade").toString());
            String unidadeMedida = (String) dados.get("unidadeMedida");
            String observacoes = (String) dados.get("observacoes");

            ItemPreDefinicao item = new ItemPreDefinicao(preDefinicao, tipoAlimento, quantidade, unidadeMedida, observacoes);
            itemRepository.save(item);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Item adicionado com sucesso"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Erro ao adicionar item: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> excluirPreDefinicao(@PathVariable UUID id) {
        try {
            PreDefinicaoCesta preDefinicao = preDefinicaoRepository.findById(id).orElse(null);
            if (preDefinicao == null) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Pré-definição não encontrada"
                ));
            }

            preDefinicao.setAtiva(false);
            preDefinicaoRepository.save(preDefinicao);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Pré-definição desativada com sucesso"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Erro ao desativar pré-definição: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/itens/{itemId}")
    public ResponseEntity<Map<String, Object>> excluirItem(@PathVariable UUID itemId) {
        try {
            itemRepository.deleteById(itemId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Item removido com sucesso"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Erro ao remover item: " + e.getMessage()
            ));
        }
    }
}

