package com.church.fooddonation.service;

import com.church.fooddonation.entity.CestaBasica;
import com.church.fooddonation.entity.CestaBasicaItem;
import com.church.fooddonation.entity.AlimentoEstoque;
import com.church.fooddonation.entity.PreDefinicaoCesta;
import com.church.fooddonation.repository.CestaBasicaRepository;
import com.church.fooddonation.repository.AlimentoEstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CestaBasicaService {

    @Autowired
    private CestaBasicaRepository cestaBasicaRepository;

    @Autowired
    private AlimentoEstoqueRepository alimentoEstoqueRepository;

    public List<CestaBasica> listarTodasCestas() {
        return cestaBasicaRepository.findAll();
    }

    public Optional<CestaBasica> buscarPorId(UUID id) {
        return cestaBasicaRepository.findById(id);
    }

    public List<CestaBasica> buscarPorStatus(String status) {
        return cestaBasicaRepository.findByStatus(status);
    }

    public List<CestaBasica> buscarCestasMontadasDisponiveis() {
        return cestaBasicaRepository.findByStatus("Montada");
    }

    public CestaBasica criarCesta(String nomeCesta) {
        CestaBasica cesta = new CestaBasica();
        cesta.setNomeCesta(nomeCesta);
        cesta.setDataMontagem(LocalDate.now());
        cesta.setStatus("Em Montagem");
        return cestaBasicaRepository.save(cesta);
    }

    public CestaBasica criarCestaComPreDefinicao(String nomeCesta, PreDefinicaoCesta preDefinicao) {
        CestaBasica cesta = new CestaBasica();
        cesta.setNomeCesta(nomeCesta);
        cesta.setDataMontagem(LocalDate.now());
        cesta.setStatus("Montada"); // Já montada com base no modelo
        cesta.setPreDefinicao(preDefinicao);
        return cestaBasicaRepository.save(cesta);
    }

    public CestaBasica salvarCesta(CestaBasica cesta) {
        return cestaBasicaRepository.save(cesta);
    }

    public long contarTodasCestas() {
        return cestaBasicaRepository.count();
    }

    public CestaBasica adicionarItemACesta(UUID cestaId, UUID alimentoEstoqueId, Double quantidade) {
        Optional<CestaBasica> cestaOpt = cestaBasicaRepository.findById(cestaId);
        Optional<AlimentoEstoque> alimentoOpt = alimentoEstoqueRepository.findById(alimentoEstoqueId);

        if (cestaOpt.isPresent() && alimentoOpt.isPresent()) {
            CestaBasica cesta = cestaOpt.get();
            AlimentoEstoque alimento = alimentoOpt.get();

            // Verificar se há estoque suficiente
            if (alimento.getQuantidadeDisponivel().doubleValue() >= quantidade) {
                CestaBasicaItem item = new CestaBasicaItem();
                item.setCestaBasica(cesta);
                item.setAlimentoEstoque(alimento);
                item.setQuantidade(java.math.BigDecimal.valueOf(quantidade));

                cesta.addItem(item);
                
                // Atualizar estoque
                alimento.setQuantidadeDisponivel(
                    alimento.getQuantidadeDisponivel().subtract(java.math.BigDecimal.valueOf(quantidade))
                );
                alimentoEstoqueRepository.save(alimento);

                return cestaBasicaRepository.save(cesta);
            } else {
                throw new RuntimeException("Estoque insuficiente para o alimento: " + alimento.getTipoAlimento());
            }
        } else {
            throw new RuntimeException("Cesta ou alimento não encontrado");
        }
    }

    public CestaBasica finalizarMontagem(UUID cestaId) {
        Optional<CestaBasica> cestaOpt = cestaBasicaRepository.findById(cestaId);
        if (cestaOpt.isPresent()) {
            CestaBasica cesta = cestaOpt.get();
            cesta.setStatus("Montada");
            return cestaBasicaRepository.save(cesta);
        } else {
            throw new RuntimeException("Cesta não encontrada");
        }
    }

    public void excluirCesta(UUID id) {
        Optional<CestaBasica> cestaOpt = cestaBasicaRepository.findById(id);
        if (cestaOpt.isPresent()) {
            CestaBasica cesta = cestaOpt.get();
            
            // Devolver itens ao estoque se a cesta não foi distribuída
            if (!"Distribuída".equals(cesta.getStatus())) {
                for (CestaBasicaItem item : cesta.getItens()) {
                    AlimentoEstoque alimento = item.getAlimentoEstoque();
                    alimento.setQuantidadeDisponivel(
                        alimento.getQuantidadeDisponivel().add(item.getQuantidade())
                    );
                    alimentoEstoqueRepository.save(alimento);
                }
            }
            
            cestaBasicaRepository.deleteById(id);
        } else {
            throw new RuntimeException("Cesta não encontrada");
        }
    }

    public Long contarPorStatus(String status) {
        return (long) cestaBasicaRepository.findByStatus(status).size();
    }
}

