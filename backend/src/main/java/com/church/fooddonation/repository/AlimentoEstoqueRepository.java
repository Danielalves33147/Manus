package com.church.fooddonation.repository;

import com.church.fooddonation.entity.AlimentoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlimentoEstoqueRepository extends JpaRepository<AlimentoEstoque, UUID> {

    List<AlimentoEstoque> findByTipoAlimento(String tipoAlimento);

    List<AlimentoEstoque> findByLocalArmazenamento(String localArmazenamento);
}

