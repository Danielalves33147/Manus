package com.church.fooddonation.controller;

import com.church.fooddonation.entity.AlimentoEstoque;
import com.church.fooddonation.repository.AlimentoEstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/estoque")
@CrossOrigin(origins = "*")
public class EstoqueController {

    @Autowired
    private AlimentoEstoqueRepository estoqueRepository;

    @GetMapping
    public ResponseEntity<List<AlimentoEstoque>> listarEstoque() {
        try {
            List<AlimentoEstoque> estoque = estoqueRepository.findItensDisponiveis();
            return ResponseEntity.ok(estoque);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/tipos")
    public ResponseEntity<List<String>> listarTiposDisponiveis() {
        try {
            List<String> tipos = estoqueRepository.findTiposAlimentosDisponiveis();
            return ResponseEntity.ok(tipos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/quantidade/{tipo}")
    public ResponseEntity<Map<String, Object>> obterQuantidadeDisponivel(@PathVariable String tipo) {
        try {
            BigDecimal quantidade = estoqueRepository.findQuantidadeDisponivelByTipo(tipo);
            if (quantidade == null) quantidade = BigDecimal.ZERO;
            
            return ResponseEntity.ok(Map.of(
                "tipoAlimento", tipo,
                "quantidadeDisponivel", quantidade
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/alertas/vencimento")
    public ResponseEntity<List<AlimentoEstoque>> obterAlertasVencimento() {
        try {
            LocalDate dataLimite = LocalDate.now().plusDays(30); // Alertar 30 dias antes
            List<AlimentoEstoque> itensVencendo = estoqueRepository.findItensProximosVencimento(dataLimite);
            return ResponseEntity.ok(itensVencendo);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/abater")
    public ResponseEntity<Map<String, Object>> abaterEstoque(@RequestBody Map<String, Object> dados) {
        try {
            String tipoAlimento = (String) dados.get("tipoAlimento");
            BigDecimal quantidadeAbater = new BigDecimal(dados.get("quantidade").toString());
            
            List<AlimentoEstoque> itensDisponiveis = estoqueRepository
                .findByTipoAlimentoAndQuantidadeDisponivelGreaterThan(tipoAlimento, BigDecimal.ZERO);
            
            // Ordenar por data de validade (FIFO - First In, First Out)
            itensDisponiveis.sort((a, b) -> a.getDataValidade().compareTo(b.getDataValidade()));
            
            BigDecimal quantidadeRestante = quantidadeAbater;
            
            for (AlimentoEstoque item : itensDisponiveis) {
                if (quantidadeRestante.compareTo(BigDecimal.ZERO) <= 0) break;
                
                BigDecimal quantidadeItem = item.getQuantidadeDisponivel();
                
                if (quantidadeItem.compareTo(quantidadeRestante) >= 0) {
                    // Item tem quantidade suficiente
                    item.setQuantidadeDisponivel(quantidadeItem.subtract(quantidadeRestante));
                    quantidadeRestante = BigDecimal.ZERO;
                } else {
                    // Item não tem quantidade suficiente, usar tudo
                    quantidadeRestante = quantidadeRestante.subtract(quantidadeItem);
                    item.setQuantidadeDisponivel(BigDecimal.ZERO);
                }
                
                estoqueRepository.save(item);
            }
            
            if (quantidadeRestante.compareTo(BigDecimal.ZERO) > 0) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Estoque insuficiente. Faltam " + quantidadeRestante + " unidades"
                ));
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Estoque abatido com sucesso"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Erro ao abater estoque: " + e.getMessage()
            ));
        }
    }
}

