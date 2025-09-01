package com.church.fooddonation.repository;

import com.church.fooddonation.entity.CestaBasica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CestaBasicaRepository extends JpaRepository<CestaBasica, UUID> {

    List<CestaBasica> findByStatus(String status);

    List<CestaBasica> findByDataMontagemBetween(LocalDate dataInicio, LocalDate dataFim);

    List<CestaBasica> findByNomeCestaContainingIgnoreCase(String nomeCesta);
}

