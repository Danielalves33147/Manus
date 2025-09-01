package com.church.fooddonation.service;

import com.church.fooddonation.dto.DoacaoDTO;
import com.church.fooddonation.entity.AlimentoEstoque;
import com.church.fooddonation.entity.Doacao;
import com.church.fooddonation.repository.AlimentoEstoqueRepository;
import com.church.fooddonation.repository.DoacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class DoacaoService {

    @Autowired
    private DoacaoRepository doacaoRepository;

    @Autowired
    private AlimentoEstoqueRepository alimentoEstoqueRepository;

    public List<DoacaoDTO> findAll() {
        return doacaoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<DoacaoDTO> findById(UUID id) {
        return doacaoRepository.findById(id)
                .map(this::convertToDTO);
    }

    public DoacaoDTO save(DoacaoDTO doacaoDTO) {
        Doacao doacao = convertToEntity(doacaoDTO);
        Doacao savedDoacao = doacaoRepository.save(doacao);
        
        // Automaticamente adiciona ao estoque
        AlimentoEstoque alimentoEstoque = new AlimentoEstoque(
                savedDoacao,
                savedDoacao.getTipoAlimento(),
                savedDoacao.getQuantidade(),
                savedDoacao.getDataValidade(),
                null // Local de armazenamento pode ser definido posteriormente
        );
        alimentoEstoqueRepository.save(alimentoEstoque);
        
        return convertToDTO(savedDoacao);
    }

    public Optional<DoacaoDTO> update(UUID id, DoacaoDTO doacaoDTO) {
        return doacaoRepository.findById(id)
                .map(doacao -> {
                    updateEntityFromDTO(doacao, doacaoDTO);
                    return convertToDTO(doacaoRepository.save(doacao));
                });
    }

    public boolean delete(UUID id) {
        if (doacaoRepository.existsById(id)) {
            doacaoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<DoacaoDTO> findByTipoAlimento(String tipoAlimento) {
        return doacaoRepository.findByTipoAlimento(tipoAlimento).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DoacaoDTO> findDoacoesProximasAoVencimento(int dias) {
        LocalDate dataLimite = LocalDate.now().plusDays(dias);
        return doacaoRepository.findByDataValidadeBefore(dataLimite).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private DoacaoDTO convertToDTO(Doacao doacao) {
        DoacaoDTO dto = new DoacaoDTO();
        dto.setId(doacao.getId());
        dto.setTipoAlimento(doacao.getTipoAlimento());
        dto.setQuantidade(doacao.getQuantidade());
        dto.setUnidadeMedida(doacao.getUnidadeMedida());
        dto.setDataRecebimento(doacao.getDataRecebimento());
        dto.setDataValidade(doacao.getDataValidade());
        dto.setOrigem(doacao.getOrigem());
        dto.setObservacoes(doacao.getObservacoes());
        return dto;
    }

    private Doacao convertToEntity(DoacaoDTO dto) {
        Doacao doacao = new Doacao();
        doacao.setTipoAlimento(dto.getTipoAlimento());
        doacao.setQuantidade(dto.getQuantidade());
        doacao.setUnidadeMedida(dto.getUnidadeMedida());
        doacao.setDataRecebimento(dto.getDataRecebimento());
        doacao.setDataValidade(dto.getDataValidade());
        doacao.setOrigem(dto.getOrigem());
        doacao.setObservacoes(dto.getObservacoes());
        return doacao;
    }

    private void updateEntityFromDTO(Doacao doacao, DoacaoDTO dto) {
        doacao.setTipoAlimento(dto.getTipoAlimento());
        doacao.setQuantidade(dto.getQuantidade());
        doacao.setUnidadeMedida(dto.getUnidadeMedida());
        doacao.setDataRecebimento(dto.getDataRecebimento());
        doacao.setDataValidade(dto.getDataValidade());
        doacao.setOrigem(dto.getOrigem());
        doacao.setObservacoes(dto.getObservacoes());
    }
}

