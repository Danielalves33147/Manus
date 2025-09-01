package com.church.fooddonation.repository;

import com.church.fooddonation.entity.Beneficiado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BeneficiadoRepository extends JpaRepository<Beneficiado, UUID> {

    Optional<Beneficiado> findByCpf(String cpf);

    List<Beneficiado> findByNomeContainingIgnoreCase(String nome);

    List<Beneficiado> findByRendaFamiliarLessThanEqual(BigDecimal rendaMaxima);

    List<Beneficiado> findByNumeroDependentesGreaterThanEqual(Integer minDependentes);

    boolean existsByCpf(String cpf);
}

