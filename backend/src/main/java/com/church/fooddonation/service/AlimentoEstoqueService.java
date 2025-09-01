package com.church.fooddonation.service;

import com.church.fooddonation.entity.AlimentoEstoque;
import com.church.fooddonation.repository.AlimentoEstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AlimentoEstoqueService {

    @Autowired
    private AlimentoEstoqueRepository alimentoEstoqueRepository;

    public List<AlimentoEstoque> listarTodosAlimentos() {
        return alimentoEstoqueRepository.findAll();
    }

    public Optional<AlimentoEstoque> buscarPorId(UUID id) {
        return alimentoEstoqueRepository.findById(id);
    }

    public List<AlimentoEstoque> buscarPorTipoAlimento(String tipoAlimento) {
        return alimentoEstoqueRepository.findByTipoAlimento(tipoAlimento);
    }

    public List<AlimentoEstoque> buscarPorLocalArmazenamento(String localArmazenamento) {
        return alimentoEstoqueRepository.findByLocalArmazenamento(localArmazenamento);
    }

    public AlimentoEstoque salvarAlimento(AlimentoEstoque alimentoEstoque) {
        return alimentoEstoqueRepository.save(alimentoEstoque);
    }

    public void excluirAlimento(UUID id) {
        alimentoEstoqueRepository.deleteById(id);
    }

    // Métodos para as queries que foram removidas temporariamente
    public List<AlimentoEstoque> findAlimentosDisponiveis() {
        // Implementação temporária, pode ser melhorada com query customizada depois
        return alimentoEstoqueRepository.findAll().stream()
                .filter(a -> a.getQuantidadeDisponivel().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());
    }

    public List<AlimentoEstoque> findAlimentosProximosAoVencimento(LocalDate dataLimite) {
        // Implementação temporária, pode ser melhorada com query customizada depois
        return alimentoEstoqueRepository.findAll().stream()
                .filter(a -> a.getDataValidade().isBefore(dataLimite.plusDays(1)) && a.getQuantidadeDisponivel().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());
    }

    public List<AlimentoEstoque> findByTipoAlimentoAndQuantidadeDisponivelGreaterThanEqual(String tipoAlimento, BigDecimal quantidadeMinima) {
        // Implementação temporária, pode ser melhorada com query customizada depois
        return alimentoEstoqueRepository.findByTipoAlimento(tipoAlimento).stream()
                .filter(a -> a.getQuantidadeDisponivel().compareTo(quantidadeMinima) >= 0)
                .collect(Collectors.toList());
    }

    public BigDecimal getTotalQuantidadeByTipoAlimento(String tipoAlimento) {
        // Implementação temporária, pode ser melhorada com query customizada depois
        return alimentoEstoqueRepository.findByTipoAlimento(tipoAlimento).stream()
                .map(AlimentoEstoque::getQuantidadeDisponivel)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

