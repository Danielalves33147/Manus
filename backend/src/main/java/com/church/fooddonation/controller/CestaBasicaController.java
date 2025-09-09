package com.church.fooddonation.controller;

import com.church.fooddonation.entity.CestaBasica;
import com.church.fooddonation.entity.PreDefinicaoCesta;
import com.church.fooddonation.entity.ItemPreDefinicao;
import com.church.fooddonation.entity.AlimentoEstoque;
import com.church.fooddonation.service.CestaBasicaService;
import com.church.fooddonation.repository.PreDefinicaoCestaRepository;
import com.church.fooddonation.repository.AlimentoEstoqueRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/cestas-basicas")
@CrossOrigin(origins = "*")
@Tag(name = "Cestas Básicas", description = "Operações relacionadas ao gerenciamento de cestas básicas")
public class CestaBasicaController {

    @Autowired
    private CestaBasicaService cestaBasicaService;

    @Autowired
    private PreDefinicaoCestaRepository preDefinicaoRepository;

    @Autowired
    private AlimentoEstoqueRepository estoqueRepository;

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

    @PostMapping
    @Operation(summary = "Criar nova cesta básica")
    public ResponseEntity<Map<String, Object>> criarCesta(@RequestBody Map<String, String> request) {
        try {
            String nomeCesta = request.get("nomeCesta");
            if (nomeCesta == null || nomeCesta.trim().isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Nome da cesta é obrigatório"
                ));
            }
            
            CestaBasica cesta = cestaBasicaService.criarCesta(nomeCesta);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cesta criada com sucesso",
                "cesta", cesta
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Erro ao criar cesta: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/montar-pre-definicao")
    @Operation(summary = "Montar cesta básica baseada em pré-definição")
    public ResponseEntity<Map<String, Object>> montarCestaPreDefinicao(@RequestBody Map<String, Object> request) {
        try {
            String nomeCesta = (String) request.get("nomeCesta");
            UUID preDefinicaoId = UUID.fromString((String) request.get("preDefinicaoId"));
            
            if (nomeCesta == null || nomeCesta.trim().isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Nome da cesta é obrigatório"
                ));
            }

            PreDefinicaoCesta preDefinicao = preDefinicaoRepository.findByIdWithItens(preDefinicaoId);
            if (preDefinicao == null) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Pré-definição não encontrada"
                ));
            }

            // Verificar se há estoque suficiente
            for (ItemPreDefinicao item : preDefinicao.getItens()) {
                BigDecimal quantidadeDisponivel = estoqueRepository.findQuantidadeDisponivelByTipo(item.getTipoAlimento());
                if (quantidadeDisponivel == null || quantidadeDisponivel.compareTo(item.getQuantidade()) < 0) {
                    return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "Estoque insuficiente para " + item.getTipoAlimento() + 
                                  ". Disponível: " + (quantidadeDisponivel != null ? quantidadeDisponivel : "0") + 
                                  ", Necessário: " + item.getQuantidade()
                    ));
                }
            }

            // Criar cesta
            CestaBasica cesta = cestaBasicaService.criarCesta(nomeCesta);

            // Abater estoque e adicionar itens à cesta
            for (ItemPreDefinicao item : preDefinicao.getItens()) {
                // Abater do estoque
                List<AlimentoEstoque> itensDisponiveis = estoqueRepository
                    .findByTipoAlimentoAndQuantidadeDisponivelGreaterThan(item.getTipoAlimento(), BigDecimal.ZERO);
                
                // Ordenar por data de validade (FIFO)
                itensDisponiveis.sort((a, b) -> a.getDataValidade().compareTo(b.getDataValidade()));
                
                BigDecimal quantidadeRestante = item.getQuantidade();
                
                for (AlimentoEstoque estoqueItem : itensDisponiveis) {
                    if (quantidadeRestante.compareTo(BigDecimal.ZERO) <= 0) break;
                    
                    BigDecimal quantidadeItem = estoqueItem.getQuantidadeDisponivel();
                    
                    if (quantidadeItem.compareTo(quantidadeRestante) >= 0) {
                        estoqueItem.setQuantidadeDisponivel(quantidadeItem.subtract(quantidadeRestante));
                        quantidadeRestante = BigDecimal.ZERO;
                    } else {
                        quantidadeRestante = quantidadeRestante.subtract(quantidadeItem);
                        estoqueItem.setQuantidadeDisponivel(BigDecimal.ZERO);
                    }
                    
                    estoqueRepository.save(estoqueItem);
                }
            }

            // Finalizar montagem da cesta
            cesta.setStatus("Montada");
            cestaBasicaService.salvarCesta(cesta);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cesta montada com sucesso baseada na pré-definição: " + preDefinicao.getNome(),
                "cesta", cesta
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Erro ao montar cesta: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/verificar-estoque/{preDefinicaoId}")
    @Operation(summary = "Verificar se há estoque suficiente para uma pré-definição")
    public ResponseEntity<Map<String, Object>> verificarEstoque(@PathVariable UUID preDefinicaoId) {
        try {
            PreDefinicaoCesta preDefinicao = preDefinicaoRepository.findByIdWithItens(preDefinicaoId);
            if (preDefinicao == null) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Pré-definição não encontrada"
                ));
            }

            boolean estoqueOk = true;
            StringBuilder detalhes = new StringBuilder();

            for (ItemPreDefinicao item : preDefinicao.getItens()) {
                BigDecimal quantidadeDisponivel = estoqueRepository.findQuantidadeDisponivelByTipo(item.getTipoAlimento());
                if (quantidadeDisponivel == null) quantidadeDisponivel = BigDecimal.ZERO;
                
                detalhes.append(item.getTipoAlimento())
                       .append(": Necessário ").append(item.getQuantidade())
                       .append(", Disponível ").append(quantidadeDisponivel);
                
                if (quantidadeDisponivel.compareTo(item.getQuantidade()) < 0) {
                    estoqueOk = false;
                    detalhes.append(" ❌");
                } else {
                    detalhes.append(" ✅");
                }
                detalhes.append("\n");
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "estoqueOk", estoqueOk,
                "detalhes", detalhes.toString()
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Erro ao verificar estoque: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir cesta básica")
    public ResponseEntity<Map<String, Object>> excluirCesta(@PathVariable UUID id) {
        try {
            cestaBasicaService.excluirCesta(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cesta excluída com sucesso"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Cesta não encontrada"
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Erro ao excluir cesta: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/criar-com-modelo")
    @Operation(summary = "Criar cesta básica usando modelo pré-definido")
    public ResponseEntity<Map<String, Object>> criarCestaComModelo(@RequestBody Map<String, String> request) {
        try {
            String nomeCesta = request.get("nomeCesta");
            String preDefinicaoIdStr = request.get("preDefinicaoId");
            
            if (nomeCesta == null || nomeCesta.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Nome da cesta é obrigatório"
                ));
            }

            if (preDefinicaoIdStr == null || preDefinicaoIdStr.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "ID da pré-definição é obrigatório"
                ));
            }

            UUID preDefinicaoId = UUID.fromString(preDefinicaoIdStr);
            PreDefinicaoCesta preDefinicao = preDefinicaoRepository.findByIdWithItens(preDefinicaoId);
            
            if (preDefinicao == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Modelo de cesta não encontrado"
                ));
            }

            // Verificar se há estoque suficiente
            for (ItemPreDefinicao item : preDefinicao.getItens()) {
                BigDecimal quantidadeDisponivel = estoqueRepository.findQuantidadeDisponivelByTipo(item.getTipoAlimento());
                if (quantidadeDisponivel == null || quantidadeDisponivel.compareTo(item.getQuantidade()) < 0) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Estoque insuficiente para " + item.getTipoAlimento() + 
                                  ". Disponível: " + (quantidadeDisponivel != null ? quantidadeDisponivel : "0") + 
                                  ", Necessário: " + item.getQuantidade()
                    ));
                }
            }

            // Criar cesta
            CestaBasica cesta = cestaBasicaService.criarCestaComPreDefinicao(nomeCesta, preDefinicao);

            // Abater estoque
            for (ItemPreDefinicao item : preDefinicao.getItens()) {
                List<AlimentoEstoque> itensDisponiveis = estoqueRepository
                    .findByTipoAlimentoAndQuantidadeDisponivelGreaterThan(item.getTipoAlimento(), BigDecimal.ZERO);
                
                BigDecimal quantidadeRestante = item.getQuantidade();
                for (AlimentoEstoque estoqueItem : itensDisponiveis) {
                    if (quantidadeRestante.compareTo(BigDecimal.ZERO) <= 0) break;
                    
                    BigDecimal quantidadeAAbater = quantidadeRestante.min(estoqueItem.getQuantidadeDisponivel());
                    estoqueItem.setQuantidadeDisponivel(estoqueItem.getQuantidadeDisponivel().subtract(quantidadeAAbater));
                    estoqueRepository.save(estoqueItem);
                    
                    quantidadeRestante = quantidadeRestante.subtract(quantidadeAAbater);
                }
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cesta criada com sucesso usando o modelo " + preDefinicao.getNome(),
                "nomeCesta", cesta.getNomeCesta(),
                "preDefinicao", Map.of(
                    "nome", preDefinicao.getNome(),
                    "descricao", preDefinicao.getDescricao()
                )
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "ID da pré-definição inválido"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Erro ao criar cesta: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/estatisticas")
    @Operation(summary = "Obter estatísticas das cestas básicas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas() {
        try {
            long totalCestas = cestaBasicaService.contarTodasCestas();
            long cestasMontadas = cestaBasicaService.contarPorStatus("Montada");
            long cestasEmMontagem = cestaBasicaService.contarPorStatus("Em Montagem");
            
            return ResponseEntity.ok(Map.of(
                "totalCestas", totalCestas,
                "cestasMontadas", cestasMontadas,
                "cestasEmMontagem", cestasEmMontagem
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

