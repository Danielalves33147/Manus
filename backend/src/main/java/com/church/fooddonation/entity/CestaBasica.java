package com.church.fooddonation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cestas_basicas")
public class CestaBasica {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank(message = "Nome da cesta é obrigatório")
    @Column(name = "nome_cesta", nullable = false)
    private String nomeCesta;

    @NotNull(message = "Data de montagem é obrigatória")
    @Column(name = "data_montagem", nullable = false)
    private LocalDate dataMontagem;

    @NotBlank(message = "Status é obrigatório")
    @Column(nullable = false, length = 50)
    private String status;

    @OneToMany(mappedBy = "cestaBasica", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CestaBasicaItem> itens = new ArrayList<>();

    // Constructors
    public CestaBasica() {}

    public CestaBasica(String nomeCesta, LocalDate dataMontagem, String status) {
        this.nomeCesta = nomeCesta;
        this.dataMontagem = dataMontagem;
        this.status = status;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNomeCesta() {
        return nomeCesta;
    }

    public void setNomeCesta(String nomeCesta) {
        this.nomeCesta = nomeCesta;
    }

    public LocalDate getDataMontagem() {
        return dataMontagem;
    }

    public void setDataMontagem(LocalDate dataMontagem) {
        this.dataMontagem = dataMontagem;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CestaBasicaItem> getItens() {
        return itens;
    }

    public void setItens(List<CestaBasicaItem> itens) {
        this.itens = itens;
    }

    public void addItem(CestaBasicaItem item) {
        itens.add(item);
        item.setCestaBasica(this);
    }

    public void removeItem(CestaBasicaItem item) {
        itens.remove(item);
        item.setCestaBasica(null);
    }
}

