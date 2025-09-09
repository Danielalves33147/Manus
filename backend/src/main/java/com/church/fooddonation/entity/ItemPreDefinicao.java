package com.church.fooddonation.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "itens_pre_definicao")
public class ItemPreDefinicao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull(message = "Pré-definição é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_definicao_id", nullable = false)
    @JsonBackReference
    private PreDefinicaoCesta preDefinicaoCesta;

    @NotBlank(message = "Tipo de alimento é obrigatório")
    @Column(name = "tipo_alimento", nullable = false)
    private String tipoAlimento;

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser positiva")
    @Column(name = "quantidade", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidade;

    @NotBlank(message = "Unidade de medida é obrigatória")
    @Column(name = "unidade_medida", nullable = false, length = 50)
    private String unidadeMedida;

    @Column(name = "observacoes")
    private String observacoes;

    // Constructors
    public ItemPreDefinicao() {}

    public ItemPreDefinicao(PreDefinicaoCesta preDefinicaoCesta, String tipoAlimento, 
                           BigDecimal quantidade, String unidadeMedida, String observacoes) {
        this.preDefinicaoCesta = preDefinicaoCesta;
        this.tipoAlimento = tipoAlimento;
        this.quantidade = quantidade;
        this.unidadeMedida = unidadeMedida;
        this.observacoes = observacoes;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PreDefinicaoCesta getPreDefinicaoCesta() {
        return preDefinicaoCesta;
    }

    public void setPreDefinicaoCesta(PreDefinicaoCesta preDefinicaoCesta) {
        this.preDefinicaoCesta = preDefinicaoCesta;
    }

    public String getTipoAlimento() {
        return tipoAlimento;
    }

    public void setTipoAlimento(String tipoAlimento) {
        this.tipoAlimento = tipoAlimento;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}

