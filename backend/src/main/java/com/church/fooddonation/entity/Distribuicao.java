package com.church.fooddonation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "distribuicoes")
public class Distribuicao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull(message = "Cesta básica é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cesta_basica_id", nullable = false)
    private CestaBasica cestaBasica;

    @NotNull(message = "Beneficiado é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiado_id", nullable = false)
    private Beneficiado beneficiado;

    @NotNull(message = "Data de distribuição é obrigatória")
    @Column(name = "data_distribuicao", nullable = false)
    private LocalDateTime dataDistribuicao;

    @Column(name = "responsavel")
    private String responsavel;

    // Constructors
    public Distribuicao() {}

    public Distribuicao(CestaBasica cestaBasica, Beneficiado beneficiado, 
                        LocalDateTime dataDistribuicao, String responsavel) {
        this.cestaBasica = cestaBasica;
        this.beneficiado = beneficiado;
        this.dataDistribuicao = dataDistribuicao;
        this.responsavel = responsavel;
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

    public Beneficiado getBeneficiado() {
        return beneficiado;
    }

    public void setBeneficiado(Beneficiado beneficiado) {
        this.beneficiado = beneficiado;
    }

    public LocalDateTime getDataDistribuicao() {
        return dataDistribuicao;
    }

    public void setDataDistribuicao(LocalDateTime dataDistribuicao) {
        this.dataDistribuicao = dataDistribuicao;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }
}

