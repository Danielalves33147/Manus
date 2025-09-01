package com.church.fooddonation.repository;

import com.church.fooddonation.entity.Distribuicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DistribuicaoRepository extends JpaRepository<Distribuicao, UUID> {

    List<Distribuicao> findByBeneficiadoId(UUID beneficiadoId);

    List<Distribuicao> findByDataDistribuicaoBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    List<Distribuicao> findByResponsavel(String responsavel);

    List<Distribuicao> findByBeneficiadoIdOrderByDataDistribuicaoDesc(UUID beneficiadoId);

    Long countByBeneficiadoId(UUID beneficiadoId);

    Long countByDataDistribuicaoBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}

