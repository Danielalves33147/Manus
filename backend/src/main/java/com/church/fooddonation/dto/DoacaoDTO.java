package com.church.fooddonation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class DoacaoDTO {

    private UUID id;

    @NotBlank(message = "Tipo de alimento é obrigatório")
    private String tipoAlimento;

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser positiva")
    private BigDecimal quantidade;

    @NotBlank(message = "Unidade de medida é obrigatória")
    private String unidadeMedida;

    @NotNull(message = "Data de recebimento é obrigatória")
    private LocalDate dataRecebimento;

    @NotNull(message = "Data de validade é obrigatória")
    private LocalDate dataValidade;

    private String origem;
    private String observacoes;

    // Constructors
    public DoacaoDTO() {}

    public DoacaoDTO(String tipoAlimento, BigDecimal quantidade, String unidadeMedida, 
                     LocalDate dataRecebimento, LocalDate dataValidade, String origem, String observacoes) {
        this.tipoAlimento = tipoAlimento;
        this.quantidade = quantidade;
        this.unidadeMedida = unidadeMedida;
        this.dataRecebimento = dataRecebimento;
        this.dataValidade = dataValidade;
        this.origem = origem;
        this.observacoes = observacoes;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public LocalDate getDataRecebimento() {
        return dataRecebimento;
    }

    public void setDataRecebimento(LocalDate dataRecebimento) {
        this.dataRecebimento = dataRecebimento;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}

