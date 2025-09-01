package com.church.fooddonation.service;

import com.church.fooddonation.dto.BeneficiadoDTO;
import com.church.fooddonation.entity.Beneficiado;
import com.church.fooddonation.repository.BeneficiadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BeneficiadoService {

    @Autowired
    private BeneficiadoRepository beneficiadoRepository;

    public List<BeneficiadoDTO> findAll() {
        return beneficiadoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<BeneficiadoDTO> findById(UUID id) {
        return beneficiadoRepository.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<BeneficiadoDTO> findByCpf(String cpf) {
        return beneficiadoRepository.findByCpf(cpf)
                .map(this::convertToDTO);
    }

    public BeneficiadoDTO save(BeneficiadoDTO beneficiadoDTO) {
        if (beneficiadoRepository.existsByCpf(beneficiadoDTO.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado no sistema");
        }
        
        Beneficiado beneficiado = convertToEntity(beneficiadoDTO);
        Beneficiado savedBeneficiado = beneficiadoRepository.save(beneficiado);
        return convertToDTO(savedBeneficiado);
    }

    public Optional<BeneficiadoDTO> update(UUID id, BeneficiadoDTO beneficiadoDTO) {
        return beneficiadoRepository.findById(id)
                .map(beneficiado -> {
                    // Verifica se o CPF não está sendo usado por outro beneficiado
                    if (!beneficiado.getCpf().equals(beneficiadoDTO.getCpf()) && 
                        beneficiadoRepository.existsByCpf(beneficiadoDTO.getCpf())) {
                        throw new IllegalArgumentException("CPF já cadastrado no sistema");
                    }
                    
                    updateEntityFromDTO(beneficiado, beneficiadoDTO);
                    return convertToDTO(beneficiadoRepository.save(beneficiado));
                });
    }

    public boolean delete(UUID id) {
        if (beneficiadoRepository.existsById(id)) {
            beneficiadoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<BeneficiadoDTO> findByNome(String nome) {
        return beneficiadoRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BeneficiadoDTO convertToDTO(Beneficiado beneficiado) {
        BeneficiadoDTO dto = new BeneficiadoDTO();
        dto.setId(beneficiado.getId());
        dto.setNome(beneficiado.getNome());
        dto.setCpf(beneficiado.getCpf());
        dto.setEndereco(beneficiado.getEndereco());
        dto.setTelefone(beneficiado.getTelefone());
        dto.setDataNascimento(beneficiado.getDataNascimento());
        dto.setNumeroDependentes(beneficiado.getNumeroDependentes());
        dto.setRendaFamiliar(beneficiado.getRendaFamiliar());
        return dto;
    }

    private Beneficiado convertToEntity(BeneficiadoDTO dto) {
        Beneficiado beneficiado = new Beneficiado();
        beneficiado.setNome(dto.getNome());
        beneficiado.setCpf(dto.getCpf());
        beneficiado.setEndereco(dto.getEndereco());
        beneficiado.setTelefone(dto.getTelefone());
        beneficiado.setDataNascimento(dto.getDataNascimento());
        beneficiado.setNumeroDependentes(dto.getNumeroDependentes());
        beneficiado.setRendaFamiliar(dto.getRendaFamiliar());
        return beneficiado;
    }

    private void updateEntityFromDTO(Beneficiado beneficiado, BeneficiadoDTO dto) {
        beneficiado.setNome(dto.getNome());
        beneficiado.setCpf(dto.getCpf());
        beneficiado.setEndereco(dto.getEndereco());
        beneficiado.setTelefone(dto.getTelefone());
        beneficiado.setDataNascimento(dto.getDataNascimento());
        beneficiado.setNumeroDependentes(dto.getNumeroDependentes());
        beneficiado.setRendaFamiliar(dto.getRendaFamiliar());
    }
}

