package com.church.fooddonation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "alimentos_estoque")
public class AlimentoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull(message = "Doação é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doacao_id", nullable = false)
    private Doacao doacao;

    @NotBlank(message = "Tipo de alimento é obrigatório")
    @Column(name = "tipo_alimento", nullable = false)
    private String tipoAlimento;

    @NotNull(message = "Quantidade disponível é obrigatória")
    @PositiveOrZero(message = "Quantidade disponível deve ser zero ou positiva")
    @Column(name = "quantidade_disponivel", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidadeDisponivel;

    @NotNull(message = "Data de validade é obrigatória")
    @Column(name = "data_validade", nullable = false)
    private LocalDate dataValidade;

    @Column(name = "local_armazenamento")
    private String localArmazenamento;

    // Constructors
    public AlimentoEstoque() {}

    public AlimentoEstoque(Doacao doacao, String tipoAlimento, BigDecimal quantidadeDisponivel, 
                           LocalDate dataValidade, String localArmazenamento) {
        this.doacao = doacao;
        this.tipoAlimento = tipoAlimento;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.dataValidade = dataValidade;
        this.localArmazenamento = localArmazenamento;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Doacao getDoacao() {
        return doacao;
    }

    public void setDoacao(Doacao doacao) {
        this.doacao = doacao;
    }

    public String getTipoAlimento() {
        return tipoAlimento;
    }

    public void setTipoAlimento(String tipoAlimento) {
        this.tipoAlimento = tipoAlimento;
    }

    public BigDecimal getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(BigDecimal quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String getLocalArmazenamento() {
        return localArmazenamento;
    }

    public void setLocalArmazenamento(String localArmazenamento) {
        this.localArmazenamento = localArmazenamento;
    }
}

