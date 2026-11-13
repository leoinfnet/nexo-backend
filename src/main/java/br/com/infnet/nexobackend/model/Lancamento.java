package br.com.infnet.nexobackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "lancamentos")
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String descricao;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoLancamento tipo;

    @Column(nullable = false, length = 100)
    private String categoria;

    @Column(name = "data_lancamento", nullable = false)
    private LocalDate dataLancamento;

    @Column(length = 1000)
    private String observacao;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    protected Lancamento() {
    }

    public Lancamento(String descricao, BigDecimal valor, TipoLancamento tipo, String categoria,
                      LocalDate dataLancamento, String observacao) {
        atualizar(descricao, valor, tipo, categoria, dataLancamento, observacao);
    }

    public void atualizar(String descricao, BigDecimal valor, TipoLancamento tipo, String categoria,
                          LocalDate dataLancamento, String observacao) {
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
        this.categoria = categoria;
        this.dataLancamento = dataLancamento;
        this.observacao = observacao;
    }

    @PrePersist
    void definirDataDeCriacao() {
        if (criadoEm == null) {
            criadoEm = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public TipoLancamento getTipo() {
        return tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
