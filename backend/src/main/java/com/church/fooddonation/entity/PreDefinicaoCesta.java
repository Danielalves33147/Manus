package com.church.fooddonation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pre_definicoes_cestas")
public class PreDefinicaoCesta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank(message = "Nome da pré-definição é obrigatório")
    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "ativa", nullable = false)
    private Boolean ativa = true;

    @OneToMany(mappedBy = "preDefinicaoCesta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemPreDefinicao> itens = new ArrayList<>();

    // Constructors
    public PreDefinicaoCesta() {}

    public PreDefinicaoCesta(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
        this.ativa = true;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public List<ItemPreDefinicao> getItens() {
        return itens;
    }

    public void setItens(List<ItemPreDefinicao> itens) {
        this.itens = itens;
    }

    public void addItem(ItemPreDefinicao item) {
        itens.add(item);
        item.setPreDefinicaoCesta(this);
    }

    public void removeItem(ItemPreDefinicao item) {
        itens.remove(item);
        item.setPreDefinicaoCesta(null);
    }
}

