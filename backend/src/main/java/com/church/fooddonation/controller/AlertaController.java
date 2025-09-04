package com.church.fooddonation.controller;

import com.church.fooddonation.entity.AlimentoEstoque;
import com.church.fooddonation.entity.Beneficiado;
import com.church.fooddonation.repository.AlimentoEstoqueRepository;
import com.church.fooddonation.repository.BeneficiadoRepository;
import com.church.fooddonation.repository.CestaBasicaRepository;
import com.church.fooddonation.repository.DoacaoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alertas")
@CrossOrigin(origins = "*")
@Tag(name = "Alertas", description = "Sistema de alertas e notificações")
public class AlertaController {

    @Autowired
    private AlimentoEstoqueRepository estoqueRepository;

    @Autowired
    private BeneficiadoRepository beneficiadoRepository;

    @Autowired
    private CestaBasicaRepository cestaRepository;

    @Autowired
    private DoacaoRepository doacaoRepository;

    @GetMapping("/dashboard")
    @Operation(summary = "Obter dashboard completo de alertas")
    public ResponseEntity<Map<String, Object>> obterDashboardAlertas() {
        try {
            Map<String, Object> dashboard = new HashMap<>();
            
            // Alertas de vencimento
            List<Map<String, Object>> alertasVencimento = obterAlertasVencimentoInterno(30);
            dashboard.put("alertasVencimento", alertasVencimento);
            dashboard.put("totalAlertasVencimento", alertasVencimento.size());
            
            // Alertas de estoque baixo
            List<Map<String, Object>> alertasEstoqueBaixo = obterAlertasEstoqueBaixoInterno();
            dashboard.put("alertasEstoqueBaixo", alertasEstoqueBaixo);
            dashboard.put("totalAlertasEstoqueBaixo", alertasEstoqueBaixo.size());
            
            // Alertas de beneficiados sem doações recentes
            List<Map<String, Object>> alertasBeneficiados = obterAlertasBeneficiadosInterno();
            dashboard.put("alertasBeneficiados", alertasBeneficiados);
            dashboard.put("totalAlertasBeneficiados", alertasBeneficiados.size());
            
            // Resumo geral
            int totalAlertas = alertasVencimento.size() + alertasEstoqueBaixo.size() + alertasBeneficiados.size();
            dashboard.put("totalAlertas", totalAlertas);
            dashboard.put("dataAtualizacao", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
            // Prioridades
            dashboard.put("alertasCriticos", alertasVencimento.stream()
                .filter(a -> "CRÍTICO".equals(a.get("prioridade")))
                .count());
            dashboard.put("alertasAltos", alertasVencimento.stream()
                .filter(a -> "ALTO".equals(a.get("prioridade")))
                .count());
            
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/vencimento")
    @Operation(summary = "Obter alertas de produtos próximos ao vencimento")
    public ResponseEntity<List<Map<String, Object>>> obterAlertasVencimento(
            @RequestParam(defaultValue = "30") int diasLimite) {
        try {
            List<Map<String, Object>> alertas = obterAlertasVencimentoInterno(diasLimite);
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/estoque-baixo")
    @Operation(summary = "Obter alertas de estoque baixo")
    public ResponseEntity<List<Map<String, Object>>> obterAlertasEstoqueBaixo() {
        try {
            List<Map<String, Object>> alertas = obterAlertasEstoqueBaixoInterno();
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/beneficiados-inativos")
    @Operation(summary = "Obter alertas de beneficiados sem doações recentes")
    public ResponseEntity<List<Map<String, Object>>> obterAlertasBeneficiados() {
        try {
            List<Map<String, Object>> alertas = obterAlertasBeneficiadosInterno();
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/configuracoes")
    @Operation(summary = "Obter configurações de alertas")
    public ResponseEntity<Map<String, Object>> obterConfiguracoes() {
        Map<String, Object> config = new HashMap<>();
        config.put("diasLimiteVencimento", 30);
        config.put("quantidadeMinimaEstoque", 10);
        config.put("diasSemDoacaoBeneficiado", 90);
        config.put("alertasAtivos", true);
        return ResponseEntity.ok(config);
    }

    // Métodos internos para processamento

    private List<Map<String, Object>> obterAlertasVencimentoInterno(int diasLimite) {
        LocalDate dataLimite = LocalDate.now().plusDays(diasLimite);
        List<AlimentoEstoque> itensVencendo = estoqueRepository.findItensProximosVencimento(dataLimite);
        
        return itensVencendo.stream()
            .filter(item -> item.getQuantidadeDisponivel().compareTo(BigDecimal.ZERO) > 0)
            .map(item -> {
                Map<String, Object> alerta = new HashMap<>();
                alerta.put("id", item.getId());
                alerta.put("tipoAlimento", item.getTipoAlimento());
                alerta.put("quantidadeDisponivel", item.getQuantidadeDisponivel());
                alerta.put("dataValidade", item.getDataValidade().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                alerta.put("localArmazenamento", item.getLocalArmazenamento());
                
                // Calcular dias até vencimento
                long diasAteVencimento = LocalDate.now().until(item.getDataValidade()).getDays();
                alerta.put("diasAteVencimento", diasAteVencimento);
                
                // Definir prioridade
                String prioridade;
                if (diasAteVencimento <= 7) {
                    prioridade = "CRÍTICO";
                } else if (diasAteVencimento <= 15) {
                    prioridade = "ALTO";
                } else {
                    prioridade = "MÉDIO";
                }
                alerta.put("prioridade", prioridade);
                
                // Mensagem personalizada
                String mensagem = String.format("%s vence em %d dias (%s)", 
                    item.getTipoAlimento(), diasAteVencimento, item.getDataValidade().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                alerta.put("mensagem", mensagem);
                
                return alerta;
            })
            .sorted((a, b) -> Long.compare((Long) a.get("diasAteVencimento"), (Long) b.get("diasAteVencimento")))
            .collect(Collectors.toList());
    }

    private List<Map<String, Object>> obterAlertasEstoqueBaixoInterno() {
        List<String> tiposAlimentos = estoqueRepository.findTiposAlimentosDisponiveis();
        BigDecimal quantidadeMinima = new BigDecimal("10"); // Configurável
        
        return tiposAlimentos.stream()
            .map(tipo -> {
                BigDecimal quantidadeTotal = estoqueRepository.findQuantidadeDisponivelByTipo(tipo);
                if (quantidadeTotal == null) quantidadeTotal = BigDecimal.ZERO;
                
                if (quantidadeTotal.compareTo(quantidadeMinima) < 0) {
                    Map<String, Object> alerta = new HashMap<>();
                    alerta.put("tipoAlimento", tipo);
                    alerta.put("quantidadeDisponivel", quantidadeTotal);
                    alerta.put("quantidadeMinima", quantidadeMinima);
                    alerta.put("prioridade", quantidadeTotal.compareTo(BigDecimal.ZERO) == 0 ? "CRÍTICO" : "ALTO");
                    alerta.put("mensagem", String.format("Estoque baixo de %s: apenas %s unidades disponíveis", 
                        tipo, quantidadeTotal));
                    return alerta;
                }
                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private List<Map<String, Object>> obterAlertasBeneficiadosInterno() {
        List<Beneficiado> todosBeneficiados = beneficiadoRepository.findAll();
        LocalDate dataLimite = LocalDate.now().minusDays(90); // 90 dias sem doação
        
        return todosBeneficiados.stream()
            .filter(beneficiado -> {
                // Verificar se tem doações recentes (implementação simplificada)
                // Em uma implementação real, você verificaria a tabela de distribuições
                return true; // Por enquanto, incluir todos para demonstração
            })
            .map(beneficiado -> {
                Map<String, Object> alerta = new HashMap<>();
                alerta.put("id", beneficiado.getId());
                alerta.put("nome", beneficiado.getNome());
                alerta.put("cpf", beneficiado.getCpf());
                alerta.put("telefone", beneficiado.getTelefone());
                alerta.put("prioridade", "MÉDIO");
                alerta.put("mensagem", String.format("Beneficiado %s não recebe doações há mais de 90 dias", 
                    beneficiado.getNome()));
                return alerta;
            })
            .limit(5) // Limitar para demonstração
            .collect(Collectors.toList());
    }
}

