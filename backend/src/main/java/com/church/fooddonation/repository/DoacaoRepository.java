package com.church.fooddonation.repository;

import com.church.fooddonation.entity.Doacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DoacaoRepository extends JpaRepository<Doacao, UUID> {

    List<Doacao> findByTipoAlimento(String tipoAlimento);

    List<Doacao> findByDataRecebimentoBetween(LocalDate dataInicio, LocalDate dataFim);

    List<Doacao> findByDataValidadeBefore(LocalDate data);

    List<Doacao> findByOrigemContainingIgnoreCase(String origem);
}

