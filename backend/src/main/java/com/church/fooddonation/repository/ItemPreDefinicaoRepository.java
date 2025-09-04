package com.church.fooddonation.repository;

import com.church.fooddonation.entity.ItemPreDefinicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemPreDefinicaoRepository extends JpaRepository<ItemPreDefinicao, UUID> {
    
    List<ItemPreDefinicao> findByPreDefinicaoCestaId(UUID preDefinicaoId);
}

