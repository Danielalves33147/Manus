package com.church.fooddonation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cestas_basicas_itens")
public class CestaBasicaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull(message = "Cesta básica é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cesta_basica_id", nullable = false)
    private CestaBasica cestaBasica;

    @NotNull(message = "Alimento do estoque é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alimento_estoque_id", nullable = false)
    private AlimentoEstoque alimentoEstoque;

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser positiva")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidade;

    // Constructors
    public CestaBasicaItem() {}

    public CestaBasicaItem(CestaBasica cestaBasica, AlimentoEstoque alimentoEstoque, BigDecimal quantidade) {
        this.cestaBasica = cestaBasica;
        this.alimentoEstoque = alimentoEstoque;
        this.quantidade = quantidade;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CestaBasica getCestaBasica() {
        return cestaBasica;
    }

    public void setCestaBasica(CestaBasica cestaBasica) {
        this.cestaBasica = cestaBasica;
    }

    public AlimentoEstoque getAlimentoEstoque() {
        return alimentoEstoque;
    }

    public void setAlimentoEstoque(AlimentoEstoque alimentoEstoque) {
        this.alimentoEstoque = alimentoEstoque;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }
}

