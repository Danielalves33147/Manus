package com.church.fooddonation.repository;

import com.church.fooddonation.entity.AlimentoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AlimentoEstoqueRepository extends JpaRepository<AlimentoEstoque, UUID> {

    List<AlimentoEstoque> findByTipoAlimento(String tipoAlimento);

    List<AlimentoEstoque> findByLocalArmazenamento(String localArmazenamento);
    
    List<AlimentoEstoque> findByTipoAlimentoAndQuantidadeDisponivelGreaterThan(String tipoAlimento, BigDecimal quantidade);
    
    @Query("SELECT a FROM AlimentoEstoque a WHERE a.dataValidade <= :dataLimite AND a.quantidadeDisponivel > 0")
    List<AlimentoEstoque> findItensProximosVencimento(@Param("dataLimite") LocalDate dataLimite);
    
    @Query("SELECT a FROM AlimentoEstoque a WHERE a.quantidadeDisponivel > 0 ORDER BY a.dataValidade ASC")
    List<AlimentoEstoque> findItensDisponiveis();
    
    @Query("SELECT DISTINCT a.tipoAlimento FROM AlimentoEstoque a WHERE a.quantidadeDisponivel > 0")
    List<String> findTiposAlimentosDisponiveis();
    
    @Query("SELECT SUM(a.quantidadeDisponivel) FROM AlimentoEstoque a WHERE a.tipoAlimento = :tipoAlimento AND a.quantidadeDisponivel > 0")
    BigDecimal findQuantidadeDisponivelByTipo(@Param("tipoAlimento") String tipoAlimento);
}

