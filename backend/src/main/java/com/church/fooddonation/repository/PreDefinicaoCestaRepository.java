package com.church.fooddonation.repository;

import com.church.fooddonation.entity.PreDefinicaoCesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PreDefinicaoCestaRepository extends JpaRepository<PreDefinicaoCesta, UUID> {
    
    List<PreDefinicaoCesta> findByAtivaTrue();
    
    @Query("SELECT p FROM PreDefinicaoCesta p LEFT JOIN FETCH p.itens WHERE p.ativa = true")
    List<PreDefinicaoCesta> findAllAtivasWithItens();
    
    @Query("SELECT p FROM PreDefinicaoCesta p LEFT JOIN FETCH p.itens WHERE p.id = :id")
    PreDefinicaoCesta findByIdWithItens(UUID id);
}

